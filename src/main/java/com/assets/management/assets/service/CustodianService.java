package com.assets.management.assets.service;

import com.assets.management.assets.client.QRGeneratorServiceProxy;
import com.assets.management.assets.model.entity.*;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.EmployeeAsset;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class CustodianService {

    @Inject
    @RestClient
    QRGeneratorServiceProxy qrProxy;

    @Inject
    Logger LOG;

    public Allocation allocate(@Valid Allocation allocation) {
        // TODO : I DONT KNOW HOW BUT IT WORKS ----- NEEDS MORE TESTING
        String queryString = "SELECT DISTINCT a FROM Allocation a LEFT JOIN FETCH a.employee e " +
                "LEFT JOIN FETCH a.asset c LEFT JOIN Transfer t ON c.id = t.asset.id " +
                "WHERE c.id = :assetId AND :allocationStatus NOT MEMBER OF a.status " +
                "AND NOT EXISTS ( SELECT 1 FROM t.status t WHERE t IN :transferStatus) ";

        Optional<Allocation> allocated = Allocation.find(
                        queryString,
                        Parameters.with("assetId", allocation.asset.id)
                                .and("allocationStatus", AllocationStatus.RETIRED)
                                .and("transferStatus", Arrays.asList(AllocationStatus.TRANSFERRED, AllocationStatus.RETIRED)))
                .firstResultOptional();

        if (allocated.isPresent()) throw new ClientErrorException(409);

        LOG.info("ALLOCATED OBJ : " + allocated.orElse(allocation).toString());

        Employee employee = Employee.findById(allocation.employee.id);
        Asset asset = Asset.getById(allocation.asset.id).firstResult();

        if (employee == null || asset == null) throw new NotFoundException();

        allocation.employee = employee;
        allocation.asset = asset;
        Allocation.persist(allocation);
        return allocation;
    }

    public Transfer transfer(@Valid Transfer transfer) {
        String queryString = "SELECT a AS assign, t AS transfer FROM Allocation a LEFT JOIN FETCH a.employee e " +
                "LEFT JOIN FETCH a.asset c LEFT JOIN Transfer t ON c.id = t.asset.id " +
                "WHERE (c.id = :assetId AND e.id = :prevCustodianId) AND  e.id <> :currentCustodianId " +
                "AND EXISTS (SELECT 1 FROM a.status s WHERE s IN :allocationStatus) " +
                "OR EXISTS (SELECT 1 FROM Transfer tr WHERE tr.asset.id = :assetId AND tr.prevCustodian.id <> :prevCustodianId " +
                "AND tr.currentCustodian.id <> :currentCustodianId AND (:transferStatus  MEMBER OF t.status) " +
                "AND EXISTS (SELECT 1 FROM a.status s WHERE s IN :secondAllocationStatus)) ";

        Tuple allocation = Panache.getEntityManager().createQuery(queryString, Tuple.class)
                .setParameter("currentCustodianId", transfer.currentCustodian.id)
                .setParameter("prevCustodianId", transfer.prevCustodian.id)
                .setParameter("assetId", transfer.asset.id)
                .setParameter("transferStatus", AllocationStatus.ALLOCATED)
                .setParameter("secondAllocationStatus", Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERRED))
                .setParameter("allocationStatus", List.of(AllocationStatus.ALLOCATED))
                .getSingleResult();

        Allocation assigned = (Allocation) allocation.get("assign");
        Transfer transferred = (Transfer) allocation.get("transfer");
        List<AllocationStatus> transferredStatus = Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERRED);

        if (assigned == null) throw new ClientErrorException(409);

        if (assigned.status.remove(AllocationStatus.ALLOCATED)) {
            assigned.status.addAll(transferredStatus);
            assigned.deallocationDate = Instant.now();
        } else if (transferred.status.remove(AllocationStatus.ALLOCATED)) {
            LOG.info("GOT INTO TRANSFER BLOCK");
            if (!transferred.currentCustodian.id.equals(transfer.prevCustodian.id))
                throw new BadRequestException();

            transferred.status.addAll(transferredStatus);
            transfer.status.add(AllocationStatus.ALLOCATED);
            Transfer.persist(transfer);
            LOG.info("PERSISTED 2ND  PARAM OBJ : " + transfer);
            return transfer;
        }

        transfer.status.add(AllocationStatus.ALLOCATED);
        Transfer.persist(transfer);
        return transfer;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Allocation> listAllocations(String searchValue, LocalDate allocationDate, String column, String direction) {
        if (searchValue != null)
            searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";


        String sortVariable = String.format("a.%s", column);
        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        String queryString = "SELECT a FROM Allocation a LEFT JOIN a.employee e LEFT JOIN e.department " +
                "LEFT JOIN e.address LEFT JOIN a.asset ast LEFT JOIN ast.category " +
                "LEFT JOIN ast.label LEFT JOIN ast.purchase p  LEFT JOIN p.supplier s " +
                "LEFT JOIN s.address " +
                "WHERE (:searchValue IS NULL OR " +
                "LOWER(e.firstName) LIKE :searchValue OR " +
                "LOWER(e.lastName) LIKE :searchValue OR " +
                "LOWER(e.workId) LIKE :searchValue OR " +
                "LOWER(e.firstName || ' ' || e.lastName) LIKE :searchValue) " +
                "AND (:date IS NULL OR a.allocationDate = :date)";

        return Allocation.find(
                queryString,
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue).and("date", allocationDate)
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Allocation> listAllTransfers(String searchValue, LocalDate transferDate, String column, String direction) {
        searchValue = searchString(searchValue);

        String sortVariable = String.format("t.%s", column);
        Sort.Direction sortDirection = panacheSort(direction);

        String queryString = "SELECT t FROM Transfer t LEFT JOIN t.prevCustodian fro LEFT JOIN fro.department " +
                "LEFT JOIN fro.address LEFT JOIN t.currentCustodian to LEFT JOIN to.department " +
                "LEFT JOIN to.address LEFT JOIN t.asset  ast LEFT JOIN ast.category " +
                "LEFT JOIN ast.label LEFT JOIN ast.purchase p  LEFT JOIN p.supplier s " +
                "LEFT JOIN s.address " +
                "WHERE (:searchValue IS NULL OR " +
                "LOWER(to.firstName) LIKE :searchValue OR " +
                "LOWER(to.lastName) LIKE :searchValue OR " +
                "LOWER(to.workId) LIKE :searchValue OR " +
                "LOWER(to.firstName || ' ' || to.lastName) LIKE :searchValue) " +
                "AND (:date IS NULL OR t.transferDate = :date)";

        return Transfer.find(
                queryString,
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue).and("date", transferDate)
        );
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public List<EmployeeAsset> getEmployeeAssets(@Valid AllocationStatus filteredStatus, @NotNull Long employeeId) {
        List<EmployeeAsset> allocatedAssets = Allocation.listAll(filteredStatus, employeeId).project(EmployeeAsset.class).list();
        List<EmployeeAsset> transferredAsset = Transfer.listAll(filteredStatus, employeeId).project(EmployeeAsset.class).list();
        List<EmployeeAsset> assets = new ArrayList<>();
        assets.addAll(allocatedAssets);
        assets.addAll(transferredAsset);

        return assets;
    }

    public void allocationQRString(@Valid Asset asset, URI allocationUri) {
        QRCode QRLabel = new QRCode();
        QRLabel.qrByteString = qrProxy.generateQrString(allocationUri);
        QRCode.persist(QRLabel);

        LOG.info("CREATED LABEL ID: " + QRLabel.id);
        asset.label = QRLabel;
        QRCode.findByIdOptional(QRLabel.id)
                .map(found -> Panache.getEntityManager().merge(asset))
                .orElseThrow(() -> new NotFoundException("Label dont exist"));
    }

    public void transferQRString(@Valid Transfer transferred, URI transferURI) {
        transferred.asset.label.qrByteString = qrProxy.generateQrString(transferURI);
        QRCode.findByIdOptional(transferred.asset.label.id)
                .map(found -> Panache.getEntityManager().merge(transferred.asset.label))
                .orElseThrow(() -> new NotFoundException("No label to update"));
    }

    // TODO: Move the private methods to util class
    private Sort.Direction panacheSort(String direction) {
        return Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;
    }

    private String searchString(String value) {
        if (value != null)
            return "%" + value.toLowerCase(Locale.ROOT) + "%";

        return null;
    }
}

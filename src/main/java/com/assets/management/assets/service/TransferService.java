package com.assets.management.assets.service;

import com.assets.management.assets.client.QRGeneratorServiceProxy;
import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.QRCode;
import com.assets.management.assets.model.entity.Transfer;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.util.PanacheUtils;
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
import java.util.List;


@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class TransferService {

    @Inject
    @RestClient
    QRGeneratorServiceProxy qrProxy;

    @Inject
    Logger LOG;

    public Transfer transfer(@Valid Transfer transfer) {
        LOG.info("GOT IN TRANS SRV : " + transfer.toString());
        String queryString = "SELECT a AS assign, t AS transfer FROM Allocation a LEFT JOIN FETCH a.employee e " +
                "LEFT JOIN FETCH a.asset c LEFT JOIN Transfer t ON c.id = t.asset.id " +
                "WHERE (c.id = :assetId AND e.id = :prevCustodianId) AND  e.id <> :currentCustodianId " +
                "AND EXISTS (SELECT 1 FROM a WHERE a.status = :allocationStatus) " +
//                "AND EXISTS (SELECT 1 FROM a.status s WHERE s IN :allocationStatus) " +
                "OR EXISTS (SELECT 1 FROM Transfer tr WHERE tr.asset.id = :assetId AND tr.employee.id <> :prevCustodianId " +
                "AND tr.newEmployee.id <> :currentCustodianId AND t.status = :transferStatus " +
//                "AND tr.newEmployee.id <> :currentCustodianId AND (:transferStatus  MEMBER OF t.status) " +
                "AND EXISTS (SELECT 1 FROM a WHERE a.status = :secondAllocationStatus)) ";
//                "AND EXISTS (SELECT 1 FROM a.status s WHERE s IN :secondAllocationStatus)) ";

        Tuple allocation = Panache.getEntityManager().createQuery(queryString, Tuple.class)
                .setParameter("currentCustodianId", transfer.newEmployee.id)
                .setParameter("prevCustodianId", transfer.employee.id)
                .setParameter("assetId", transfer.asset.id)
                .setParameter("transferStatus", AllocationStatus.ALLOCATED)
                .setParameter("secondAllocationStatus", AllocationStatus.TRANSFERRED)
                .setParameter("allocationStatus", List.of(AllocationStatus.ALLOCATED))
                .getSingleResult();

        Allocation assigned = (Allocation) allocation.get("assign");
        Transfer transferred = (Transfer) allocation.get("transfer");

        LOG.info("BEFORE : " + assigned.toString());
        AllocationStatus transferredStatus = AllocationStatus.TRANSFERRED;
        if (assigned == null) throw new ClientErrorException(409);
        transfer.asset = (transferred != null) ? transferred.asset : assigned.asset;

        LOG.info("AFTER : " + transfer.toString());

        if (assigned.status.equals(AllocationStatus.ALLOCATED)) {
            assigned.status = transferredStatus;
            assigned.deallocationDate = Instant.now();
        } else if (transferred != null && transferred.status.equals(AllocationStatus.ALLOCATED)) {
            if (!transferred.newEmployee.id.equals(transfer.employee.id))
                throw new BadRequestException();

            transferred.status = transferredStatus;
            transfer.status = AllocationStatus.ALLOCATED;
            Transfer.persist(transfer);
            LOG.info("PERSISTED 2ND  PARAM OBJ : " + transfer);
            return transfer;
        }

        transfer.status = AllocationStatus.ALLOCATED;
        Transfer.persist(transfer);
        return transfer;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Allocation> listAllTransfers(String searchValue, LocalDate transferDate, String column, String direction) {
        searchValue = PanacheUtils.searchString(searchValue);
        String sortVariable = String.format("t.%s", column);
        Sort.Direction sortDirection = PanacheUtils.panacheSort(direction);

        String queryString = "SELECT t FROM Transfer t LEFT JOIN t.employee fro LEFT JOIN fro.department " +
                "LEFT JOIN fro.address LEFT JOIN t.newEmployee to LEFT JOIN to.department " +
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

    public void transferQRString(@Valid Transfer transferred, URI transferURI) {
        transferred.asset.label.qrByteString = qrProxy.generateQrString(transferURI);
        QRCode.findByIdOptional(transferred.asset.label.id)
                .map(found -> Panache.getEntityManager().merge(transferred.asset.label))
                .orElseThrow(() -> new NotFoundException("No label to update"));
    }

    public void updateTransfer(@Valid Transfer transfer, @NotNull Long transferId) {
        Allocation.findByIdOptional(transferId)
                .map(found -> Panache.getEntityManager().merge(transfer))
                .orElseThrow(() -> new NotFoundException("Transfer record dont exist"));
    }

    public void deleteTransfer(@NotNull Long transferId) {
        Panache.getEntityManager()
                .getReference(Transfer.class, transferId)
                .delete();
    }
}

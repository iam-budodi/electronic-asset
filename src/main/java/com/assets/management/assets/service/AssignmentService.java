package com.assets.management.assets.service;

import com.assets.management.assets.client.QRGeneratorServiceProxy;
import com.assets.management.assets.model.entity.*;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.EmployeeAsset;
import com.assets.management.assets.util.PanacheUtils;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class AssignmentService {

    @Inject
    @RestClient
    QRGeneratorServiceProxy qrProxy;

    @Inject
    Logger LOG;

    public Allocation allocate(@Valid Allocation allocation) {
        // TODO : I DONT KNOW HOW BUT IT WORKS ----- NEEDS MORE TESTING
        String queryString = "SELECT DISTINCT a FROM Allocation a LEFT JOIN FETCH a.employee e " +
                "LEFT JOIN FETCH a.asset c LEFT JOIN Transfer t ON c.id = t.asset.id " +
                "WHERE c.id = :assetId AND a.status = :allocationStatus " +
                "AND NOT EXISTS ( SELECT 1 FROM t WHERE t.status = :transferStatus OR t.status = :retiredStatus) ";

        Optional<Allocation> allocated = Allocation.find(
                        queryString,
                        Parameters.with("assetId", allocation.asset.id)
//                                .and("allocationStatus", AllocationStatus.RETIRED) // TODO: THIS CAUSES ERROR
                                .and("allocationStatus", AllocationStatus.ALLOCATED)
                                .and("transferStatus", AllocationStatus.TRANSFERRED)
                                .and("retiredStatus", AllocationStatus.RETIRED))
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

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Allocation> listAllocations(String searchValue, LocalDate allocationDate, String column, String direction) {
        searchValue = PanacheUtils.searchString(searchValue);
        String sortVariable = String.format("a.%s", column);
        Sort.Direction sortDirection = PanacheUtils.panacheSort(direction);

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
    public List<EmployeeAsset> getEmployeeAssets(@Valid AllocationStatus filteredStatus, @NotNull String workId) {
        List<EmployeeAsset> allocatedAssets = Allocation.listAll(filteredStatus, workId).project(EmployeeAsset.class).list();
        List<EmployeeAsset> transferredAsset = Transfer.listAll(filteredStatus, workId).project(EmployeeAsset.class).list();
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

    public void updateAllocation(@Valid Allocation allocation, @NotNull Long allocationId) {
        Allocation.findByIdOptional(allocationId)
                .map(found -> Panache.getEntityManager().merge(allocation))
                .orElseThrow(() -> new NotFoundException("Allocation dont exist"));
    }

    public void deleteAllocation(@NotNull Long allocationId) {
        Panache.getEntityManager()
                .getReference(Allocation.class, allocationId)
                .delete();
    }
}

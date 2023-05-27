package com.assets.management.assets.service;

import com.assets.management.assets.client.QRGeneratorServiceProxy;
import com.assets.management.assets.model.entity.*;
import com.assets.management.assets.model.valueobject.AllocationStatus;
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
import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EmployeeService {

    @Inject
    @RestClient
    QRGeneratorServiceProxy generatorProxy;

    @Inject
    Logger LOG;

    public Employee addEmployee(@Valid Employee employee) {
        employee.address.employee = employee;
        employee.address.id = employee.id;
        Employee.persist(employee);
        return employee;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public PanacheQuery<Employee> listEmployees(String searchValue, LocalDate date, String column, String direction) {
        if (searchValue != null) searchValue = "%" + searchValue.toLowerCase(Locale.ROOT) + "%";

        String sortVariable = String.format("e.%s", column);
        Sort.Direction sortDirection = Objects.equals(direction.toLowerCase(Locale.ROOT), "desc")
                ? Sort.Direction.Descending
                : Sort.Direction.Ascending;

        String queryString = "SELECT e FROM Employee e LEFT JOIN e.department d LEFT JOIN e.address a " +
                "WHERE (:searchValue IS NULL OR LOWER(e.firstName) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(e.lastName) LIKE :searchValue " +
                "OR :searchValue IS NULL OR LOWER(e.workId) LIKE :searchValue " +
                "OR LOWER(e.firstName) || ' ' || LOWER(e.lastName) LIKE :searchValue " +
                "OR LOWER(e.email) LIKE :searchValue) ";

        if (date != null) queryString += "AND e.hireDate = :date";
        else queryString += "AND (:date IS NULL OR e.hireDate = :date)";

        LOG.info("SORT VARIABLE : " + sortVariable + " AND DIRECTION " + sortDirection);
        return Employee.find(
                queryString,
//                Sort.by("e.firstName").and("e.lastName").and("e.hireDate"),
                Sort.by(sortVariable, sortDirection),
                Parameters.with("searchValue", searchValue).and("date", date)
        );
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Employee> findById(@NotNull Long employeeId) {
        return Employee.find(
                        "FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.address "
                                + "WHERE e.id = :employeeId ", Parameters.with("employeeId", employeeId))
                .firstResultOptional();
    }

//    public Allocation allocateAsset(@Valid Allocation allocation, @NotNull Long employeeId) {
//        // TODO : I DONT KNOW HOW BUT IT WORKS ----- NEEDS MORE TESTING
//        Optional<Allocation> allocated = Allocation.find("SELECT DISTINCT a FROM Allocation a "
//                                + "LEFT JOIN FETCH a.employee e "
//                                + "LEFT JOIN FETCH a.asset c "
//                                + "LEFT JOIN Transfer t ON c.id = t.asset.id "
//                                + "WHERE c.id = :assetId "
//                                + "AND :allocationStatus NOT MEMBER OF a.status "
//                                + "AND NOT EXISTS ( SELECT 1 FROM t.status t WHERE t IN :transferStatus) ",
////				+ "AND a.status <> :status",  original and working
//                        Parameters.with("assetId", allocation.asset.id)
//                                .and("allocationStatus", AllocationStatus.RETIRED)
//                                .and("transferStatus", Arrays.asList(AllocationStatus.TRANSFERRED, AllocationStatus.RETIRED)))
//                .firstResultOptional();
//
//        if (allocated.isPresent()) throw new ClientErrorException(409);
//
//        LOG.info("ALLOCATED OBJ : " + allocated.orElse(allocation).toString());
//
//        Employee employee = Employee.findById(employeeId);
//        Asset asset = Asset.getById(allocation.asset.id).firstResult();
//
//        if (employee == null || asset == null) throw new NotFoundException();
//
//        allocation.employee = employee;
//        allocation.asset = asset;
//        Allocation.persist(allocation);
//        return allocation;
//    }

//    public Transfer transferAsset(@Valid Transfer transfer, @NotNull Long fromEmployeeId) {
//        Tuple allocationTransfer = Panache.getEntityManager().createQuery("SELECT a AS allocation, t AS transfer FROM Allocation a "
//                        + "LEFT JOIN FETCH a.employee e "
//                        + "LEFT JOIN FETCH a.asset c "
//                        + "LEFT JOIN Transfer t ON c.id = t.asset.id "
//                        + "WHERE (c.id = :assetId AND e.id = :fromEmployeeId) "
//                        + "AND  e.id <> :toEmployeeId "
//                        + "AND EXISTS ( SELECT 1 FROM a.status s WHERE s IN :allocationStatus) "
//                        + "OR  EXISTS ( "
//                        + "SELECT 1 FROM Transfer tr "
//                        + "WHERE tr.asset.id = :assetId "
//                        + "AND tr.prevCustodian.id <> :fromEmployeeId "
//                        + "AND tr.currentCustodian.id <> :toEmployeeId "
//                        + "AND (:transferStatus  MEMBER OF t.status) "
//                        + "AND EXISTS ( SELECT 1 FROM a.status s WHERE s IN :secondAllocationStatus)"
//                        + ") ", Tuple.class)
//                .setParameter("toEmployeeId", transfer.currentCustodian.id)
//                .setParameter("fromEmployeeId", transfer.prevCustodian.id)
//                .setParameter("assetId", transfer.asset.id)
//                .setParameter("transferStatus", AllocationStatus.ALLOCATED)
//                .setParameter("secondAllocationStatus", Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERRED))
//                .setParameter("allocationStatus", List.of(AllocationStatus.ALLOCATED))
//                .getSingleResult();
//
//        Allocation allocated = (Allocation) allocationTransfer.get("allocation");
//        Transfer transferred = (Transfer) allocationTransfer.get("transfer");
//        List<AllocationStatus> transferredStatus = Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERRED);
//
//        if (allocated == null) throw new ClientErrorException(409);
//        if (transfer.currentCustodian == null) throw new NotFoundException();
//
//        if (allocated.status.remove(AllocationStatus.ALLOCATED)) {
//            allocated.status.addAll(transferredStatus);
//            allocated.deallocationDate = Instant.now();
//        } else if (transferred.status.remove(AllocationStatus.ALLOCATED)) {
//            if (!transferred.currentCustodian.id.equals(transfer.prevCustodian.id)) throw new BadRequestException();
//
//            transferred.status.addAll(transferredStatus);
//            transfer.status.add(AllocationStatus.ALLOCATED);
//            Transfer.persist(transfer);
//            LOG.info("PERSISTED 2ND  PARAM OBJ : " + transfer);
//            return transfer;
//        }
//
//        transfer.status.add(AllocationStatus.ALLOCATED);
//        Transfer.persist(transfer);
//        return transfer;
//    }

//    @Transactional(Transactional.TxType.SUPPORTS)
//    public List<Object> employeeAssets(@Valid AllocationStatus filteredStatus, @NotNull Long employeeId) {
//        List<Allocation> allocations = Allocation.listAll(filteredStatus, employeeId);
//        List<Transfer> transfers = Transfer.listAll(filteredStatus, employeeId);
//        List<Object> allocationsOrTransfers = new ArrayList<>();
//        allocationsOrTransfers.addAll(allocations);
//        allocationsOrTransfers.addAll(transfers);
//
//        return allocationsOrTransfers;
//    }

    public void updateEmployee(@Valid Employee employee, @NotNull Long empId) {
        Employee.findByIdOptional(empId)
                .map(found -> Panache.getEntityManager().merge(employee))
                .orElseThrow(() -> new NotFoundException("Employee dont exist"));
    }

    public void deleteEmployee(@NotNull Long empId) {
        Panache.getEntityManager()
                .getReference(Employee.class, empId)
                .delete();
    }

    // TODO: FIND A WAY TO OPTIMIZE THESE TWO METHODS
//    public void updateAssetWithlabel(@Valid Asset asset, URI allocationUri) {
//        QRCode label = new QRCode();
//        label.qrByteString = generatorProxy.generateQrString(allocationUri);
//        QRCode.persist(label);
//
//        LOG.info("CREATED LABEL ID: " + label.id);
//        asset.label = label;
//        QRCode.findByIdOptional(label.id)
//                .map(found -> Panache.getEntityManager().merge(asset))
//                .orElseThrow(() -> new NotFoundException("Label dont exist"));
//    }
//
//    public void updateTranferedAssetWithlabel(@Valid Transfer transfered, URI transferURI) {
//        transfered.asset.label.qrByteString = generatorProxy.generateQrString(transferURI);
//        QRCode.findByIdOptional(transfered.asset.label.id)
//                .map(found -> Panache.getEntityManager().merge(transfered.asset.label))
//                .orElseThrow(() -> new NotFoundException("Label dont exist"));
//    }
}

package com.assets.management.assets.service;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.assets.management.assets.client.QrProxy;
import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.QRCode;
import com.assets.management.assets.model.entity.Transfer;
import com.assets.management.assets.model.valueobject.AllocationStatus;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EmployeeService {
	
	@Inject
	@RestClient
	QrProxy generatorProxy;
	
	@Inject
	Logger LOG;

	public Employee addEmployee(@Valid Employee employee) {
		employee.address.employee = employee;
		employee.address.id = employee.id;
		Employee.persist(employee);
		return employee;
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Employee> listEmployees(Integer page, Integer size) {
		return Employee.find(
				"FROM Employee e LEFT JOIN FETCH e.department d LEFT JOIN FETCH e.address ", 
				Sort.by("d.name").and("e.hireDate").and("e.firstName").and("e.lastName"))
				.page(page, size)
				.list();
	}
	
	@Transactional(Transactional.TxType.SUPPORTS)
	public Optional<Employee> findById(@NotNull Long employeeId) {
		return Employee.find(
				"FROM Employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH e.address "
				+ "WHERE e.id = :employeeId ", Parameters.with("employeeId", employeeId))
				.firstResultOptional();
	}
	
	public Allocation allocateAsset(@Valid Allocation allocation,  @NotNull Long employeeId) {
		// TODO : I DONT KNOW HOW BUT IT WORKS ----- NEEDS MORE TESTING
		Optional<Allocation>  allocated =  Allocation.find("SELECT DISTINCT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH a.asset c "
				+ "LEFT JOIN Transfer t ON c.id = t.asset.id "
				+ "WHERE c.id = :assetId "
				+ "AND :allocationStatus NOT MEMBER OF a.status "
				+ "AND NOT EXISTS ( SELECT 1 FROM t.status t WHERE t IN :transferStatus) ",
//				+ "AND a.status <> :status",  original and working
				Parameters.with("assetId", allocation.asset.id)
				.and("allocationStatus", AllocationStatus.RETIRED)
				.and("transferStatus", Arrays.asList(AllocationStatus.TRANSFERED, AllocationStatus.RETIRED)))
				.firstResultOptional();
		
		if (allocated.isPresent()) throw new ClientErrorException(409);
		
		LOG.info("ALLOCATED OBJ : " + allocated.orElse(allocation).toString());
		
		Employee employee = Employee.findById(employeeId);
		Asset asset =  Asset.findById(allocation.asset.id);
		
		if (employee == null || asset == null) throw new NotFoundException();

		allocation.employee = employee;
		allocation.asset = asset;
		Allocation.persist(allocation);
		return allocation;
	}
	 
	public Transfer transferAsset(@Valid Transfer transfer, @NotNull Long fromEmployeeId) {
		Tuple allocationTransfer =  Panache.getEntityManager().createQuery("SELECT a AS allocation, t AS transfer FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH a.asset c "
				+ "LEFT JOIN Transfer t ON c.id = t.asset.id "
				+ "WHERE (c.id = :assetId AND e.id = :fromEmployeeId) "
				+ "AND  e.id <> :toEmployeeId "
				+ "AND EXISTS ( SELECT 1 FROM a.status s WHERE s IN :allocationStatus) "
				+ "OR  EXISTS ( "
					+ "SELECT 1 FROM Transfer tr "
					+ "WHERE tr.asset.id = :assetId "
					+ "AND tr.fromEmployee.id <> :fromEmployeeId "
					+ "AND tr.toEmployee.id <> :toEmployeeId "
					+ "AND (:transferStatus  MEMBER OF t.status) "
					+ "AND EXISTS ( SELECT 1 FROM a.status s WHERE s IN :secondAllocationStatus)"
				+ ") ", Tuple.class)
		.setParameter("toEmployeeId", transfer.toEmployee.id)
		.setParameter("fromEmployeeId", transfer.fromEmployee.id)
		.setParameter("assetId", transfer.asset.id)
		.setParameter("transferStatus", AllocationStatus.ALLOCATED)
		.setParameter("secondAllocationStatus", Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERED))
		.setParameter("allocationStatus", Arrays.asList(AllocationStatus.ALLOCATED))
		.getSingleResult();
		
		Allocation allocated = (Allocation) allocationTransfer.get("allocation");
		Transfer transfered = (Transfer) allocationTransfer.get("transfer");
		List<AllocationStatus> transferedStatus = Arrays.asList(AllocationStatus.DEALLOCATED, AllocationStatus.TRANSFERED);

		if (allocated == null)  throw new ClientErrorException(409);
		if (transfer.toEmployee == null) throw new NotFoundException();
		 
		if (allocated.status.remove(AllocationStatus.ALLOCATED))  {
			allocated.status.addAll(transferedStatus);
			allocated.deallocationDate = Instant.now();
		} 
		else if (transfered.status.remove(AllocationStatus.ALLOCATED)) {
			if (!transfered.toEmployee.id.equals(transfer.fromEmployee.id)) throw new BadRequestException();
			
			transfered.status.addAll(transferedStatus);
			transfer.status.add(AllocationStatus.ALLOCATED);
			Transfer.persist(transfer);
			LOG.info("PERSISTED 2ND  PARAM OBJ : " + transfer.toString());
			return transfer;
		}
		
		transfer.status.add(AllocationStatus.ALLOCATED);
		Transfer.persist(transfer);
		return transfer;
	}
	
	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Object> employeeAssets(@Valid AllocationStatus filteredStatus, @NotNull Long employeeId) {
		List<Allocation> allocations = Allocation.listAllandFilterQuery(filteredStatus, employeeId);
		List<Transfer> transfers = Transfer.listAllandFilterQuery(filteredStatus, employeeId);
		List<Object> allocationsOrTransfers = new ArrayList<>();
		allocationsOrTransfers.addAll(allocations);
		allocationsOrTransfers.addAll(transfers);
		
		return allocationsOrTransfers;
	}

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
	public void updateAssetWithlabel(@Valid Asset asset, URI allocationUri) {
		QRCode label = new QRCode();
		label.qrByteString = generatorProxy.generateQrString(allocationUri);
		QRCode.persist(label);

		LOG.info("CREATED LABEL ID: " + label.id);
		asset.label = label; 
		QRCode.findByIdOptional(label.id)
		.map(found -> Panache.getEntityManager().merge(asset))
		.orElseThrow(() -> new NotFoundException("Label dont exist"));
	}
	
	public void updateTranferedAssetWithlabel(@Valid Transfer transfered, URI transferURI) {
		transfered.asset.label.qrByteString = generatorProxy.generateQrString(transferURI);
		QRCode.findByIdOptional(transfered.asset.label.id)
				.map(found -> Panache.getEntityManager().merge(transfered.asset.label))
				.orElseThrow(() -> new NotFoundException("Label dont exist"));
	}
	
	

}

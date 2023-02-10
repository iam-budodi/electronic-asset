package com.assets.management.assets.service;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.entity.Allocation;
import com.assets.management.assets.model.entity.Asset;
import com.assets.management.assets.model.entity.Employee;
import com.assets.management.assets.model.entity.QRCode;
import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.util.QRGenerator;
import com.google.zxing.WriterException;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EmployeeService {

	@Inject
	QRGenerator qrGenerator;
	
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
		return Employee.find("FROM Employee e "
				+ "LEFT JOIN FETCH e.department d "
				+ "LEFT JOIN FETCH e.address ", Sort.by("d.name").and("e.hireDate").and("e.firstName").and("e.lastName"))
				.page(page, size)
				.list();
	}
	
	@Transactional(Transactional.TxType.SUPPORTS)
	public Optional<Employee> findById(@NotNull Long employeeId) {
		return Employee.find("FROM Employee e "
				+ "LEFT JOIN FETCH e.department "
				+ "LEFT JOIN FETCH e.address "
				+ "WHERE e.id = :employeeId ", 
				Parameters.with("employeeId", employeeId))
				.firstResultOptional();
	}
	
	public Allocation allocateAsset(@Valid Allocation allocation,  @NotNull Long employeeId) {
		Optional<Allocation>  allocated =  Allocation.find("SELECT DISTINCT a FROM Allocation a "
				+ "LEFT JOIN FETCH a.employee e "
				+ "LEFT JOIN FETCH a.asset c "
				+ "LEFT JOIN Transfer t ON c.id = t.asset.id "
				+ "WHERE c.id = :assetId "
				+ "AND a.status <> :status", 
				Parameters.with("assetId", allocation.asset.id)
				.and("status", AllocationStatus.DEALLOCATED))
				.firstResultOptional();
		
		if (allocated.isPresent()) throw new ClientErrorException(409);
		
		Employee employee = Employee.findById(employeeId);
		Asset asset =  Asset.findById(allocation.asset.id);
		
		if (employee == null || asset == null) throw new NotFoundException();

		allocation.employee = employee;
		allocation.asset = asset;
		Allocation.persist(allocation);
		return allocation;
	}

	public void updateEmployee(@Valid Employee employee, @NotNull Long empId) {
		employee.address = null;
		Employee.findByIdOptional(empId)
			.map(found -> Panache.getEntityManager().merge(employee))
			.orElseThrow(() -> new NotFoundException("Employee dont exist"));
	}

	public void deleteEmployee(@NotNull Long empId) {
		Panache.getEntityManager()
			.getReference(Employee.class, empId)
			.delete();
	}
	
	// TODO: MOVE THE GENERATE QR METHOD TO OTHER SERVICE AND REMOVE THESE EXCEPTION
	public void updateAssetWithlabel(@Valid Asset asset, URI uri) throws WriterException, IOException {
		QRCode label = new QRCode();
		label.qrByteString = qrGenerator.generateQrString(uri);
		QRCode.persist(label);
		asset.label = label; 
	}
}

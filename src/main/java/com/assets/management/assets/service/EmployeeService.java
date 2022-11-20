package com.assets.management.assets.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Item;
import com.assets.management.assets.model.Employee;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EmployeeService {

	@Inject
	Logger LOG;

	public Employee addEmployee(@Valid Employee employee) {
		employee.address.employee = employee;
		employee.address.id = employee.id;
		
		Employee.persist(employee);
		return employee;
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Employee> listAllEmployees(Integer page, Integer size) {
		return Employee.find("from Employee em").page(page, size).list();
	}

	public Employee updateById(@Valid Employee employee, @NotNull Long empId) {
		Panache.getEntityManager().getReference(Employee.class, empId);
		return Panache.getEntityManager().merge(employee);
	}

	public void deleteById(@NotNull Long empId) {
		Panache.getEntityManager().getReference(Employee.class, empId).delete();
	}

	public Long deleteAll() {
		return Employee.deleteAll();
	}

	// Move to assignment  API
	public void assignAsset(@Valid Item asset, @NotNull Long candidateId) {
		Optional<Employee> optional = Employee.findByIdOptional(candidateId);
		LOG.info("Is EndUser Present " + optional.get());
		Employee endUser = optional.orElseThrow(
		        () -> new BadRequestException("Candidate dont exist")
		);
//
//		asset.endUser = endUser;
//		asset.employDate = Instant.now();
		Panache.getEntityManager().merge(asset);
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Item> getAllAssets(Long candidateId) {
		return Item.find("endUser.id = ?1", candidateId).list();
	}

	public void unAssignAsset(@NotNull Long candidateId, String serialNumber) {
		Item asset = Item.find(
		        "endUser.id = ?1 and serialNumber = ?2", candidateId,
		        serialNumber
		).firstResult();
		if (asset == null)
			throw new NotFoundException("Record not found!");
//		asset.endUser = null;

		Panache.getEntityManager().merge(asset);
	}
}

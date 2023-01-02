package com.assets.management.assets.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.assets.management.assets.model.Employee;
import com.assets.management.assets.qualifier.WorkId;
import com.assets.management.assets.util.NumberGenerator;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EmployeeService {
	
	@Inject
	@WorkId
	NumberGenerator workIdGenerator;
	
	@Inject
	Logger LOG;

	public Employee addEmployee(@Valid Employee employee) {
		Long employeeId;
		Employee emp = Employee.find("FROM Employee WHERE id = (SELECT MAX(id) FROM Employee)").firstResult();
		if (emp == null) employeeId = 1L;
		else employeeId = emp.id;
		LOG.info("The Latest Record: " + emp);
		employee.workId = workIdGenerator.generateNumber(employeeId);
		employee.address.employee = employee;
		employee.address.id = employee.id;
		Employee.persist(employee);
		return employee;
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Employee> listEmployees(Integer page, Integer size) {
		return Employee.find("FROM Employee", Sort.by("firstName").and("lastName"))
				.page(page, size)
				.list();
	}

	public void updateById(@Valid Employee employee, @NotNull Long empId) {
		employee.address = null;
		Employee.findByIdOptional(empId)
			.map(found -> Panache.getEntityManager().merge(employee))
			.orElseThrow(() -> new NotFoundException("Employee dont exist"));
	}

	public void deleteById(@NotNull Long empId) {
		Panache.getEntityManager()
			.getReference(Employee.class, empId)
			.delete();
	}
}

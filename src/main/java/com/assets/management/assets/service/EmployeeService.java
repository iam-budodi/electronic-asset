package com.assets.management.assets.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import com.assets.management.assets.model.Employee;

import io.quarkus.hibernate.orm.panache.Panache;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class EmployeeService {

	public Employee addEmployee(@Valid Employee employee) {
		employee.address.employee = employee;
		employee.address.id = employee.id;
		Employee.persist(employee);
		return employee;
	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public List<Employee> listEmployees(Integer page, Integer size) {
		return Employee
				.find("from Employee em")
				.page(page, size)
				.list();
	}

	public void updateById(@Valid Employee employee, @NotNull Long empId) {
		Employee.findByIdOptional(empId).map(
		        found -> Panache.getEntityManager().merge(employee)
		).orElseThrow(
				() -> new NotFoundException("Employee dont exist")
				);
	}

	public void deleteById(@NotNull Long empId) {
		Panache.getEntityManager()
			.getReference(Employee.class, empId)
			.delete();
	}

	public Long deleteAll() {
		return Employee.deleteAll();
	}
}

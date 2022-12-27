package com.assets.management.assets.model;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "employees")
public class Employee extends Person {

	@NotNull
	@Column(name = "work_id")
	public String workId;

	@NotNull
	@Column(name = "birthdate")
	public LocalDate dateOfBirth;

	@NotNull
	@Column(name = "hire_date")
	public LocalDate hireDate;

	public String status;

	@Transient
	public Integer age;
	
	@Transient
	public Integer timeOfService;
	
	@Transient
	public LocalDate retireAt;

	@ManyToOne(fetch = FetchType.LAZY)
	public Department department;

	@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Address address;

	@PostLoad
	@PostPersist
	@PostUpdate
	protected void calculateAgeAndRetireDate() {
		if (hireDate == null) {
			retireAt = null;
			return;
		} else if (dateOfBirth == null) {
			age = null;
			return;
		}
		
		timeOfService = Period.between(hireDate, LocalDate.now()).getYears();
		age = Period.between(dateOfBirth, LocalDate.now()).getYears();
		retireAt = LocalDate.now().plusYears(60 - age);
	}
}

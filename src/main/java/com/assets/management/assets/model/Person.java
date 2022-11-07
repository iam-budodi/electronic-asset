package com.assets.management.assets.model;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@MappedSuperclass
public class Person extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Column(name = "first_name", length = 64, nullable = false)
	public String firstName;

	@Size(min = 2, max = 64)
	@Column(name = "middle_name", length = 64)
	public String middleName;

	@NotNull
	@Size(min = 2, max = 64) 
	@Column(name = "last_name", length = 64)
	public String lastName;
	
	@Enumerated(EnumType.STRING)
	public Gender gender;
	
	@NotNull
	@Column(name = "birthdate")
	public LocalDate DoB;

	@NotNull
	@Column(name = "phone_number")
	public String mobile;
	
	@NotNull
	@Column
	public String email;
	
	@Transient
	public Integer age;

	@PostLoad  
	@PostPersist
	@PostUpdate
	protected void calculateAge() {
		if (DoB == null) {
			age = null;
			return;
		}

		age = Period.between(DoB, LocalDate.now()).getYears();
	}
}

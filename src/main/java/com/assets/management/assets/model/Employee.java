package com.assets.management.assets.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
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
	@Column(name = "employee_id")
	public String employeeId;

	@NotNull
	@Column(name = "start_at")
	public LocalDate startAt;

	public String status;

	@Column(name = "created_at")
	public Instant createdAt;

	@Column(name = "updated_at")
	public Instant updatedAt;

	@Column(name = "created_by")
	public String createdBy;

	@Column(name = "updated_by")
	public String updatedBy;

	@Column(name = "birth_date")
	public LocalDate birthDate;

	@ManyToOne(fetch = FetchType.LAZY)
	public Department department;

	@Transient
	public LocalDate endAt;

	@PostLoad
	@PostPersist
	@PostUpdate
	protected void retireDate() {
		if (startAt == null) {
			endAt = null;
			return;
		}

		Period timeOfService = Period.between(startAt, LocalDate.now());
		endAt = startAt.plus(timeOfService);
	}
}

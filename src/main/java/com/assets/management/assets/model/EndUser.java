package com.assets.management.assets.model;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "end_users")
public class EndUser extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 50)
	@Column(name = "first_name", length = 50)
	public String firstName;

	@NotNull
	@Size(min = 2, max = 50)
	@Column(name = "last_name", length = 50)
	public String lastName;
	public String address;

	@NotNull
	public String email;

	@NotNull
	public String phone;

	@Column(name = "birth_date")
	public LocalDate birthDate;

	@Transient
	public Integer age;

	@PostLoad
	@PostPersist
	@PostUpdate
	protected void calculateAge() {
		if (birthDate == null) {
			age = null;
			return;
		}

		age = Period.between(birthDate, LocalDate.now()).getYears();
	}
}

package com.assets.management.assets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "addresses")
public class Address extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 32)
	@Column(length = 32, nullable = false)
	public String street;

	@NotNull
	@Size(min = 2, max = 32)
	@Column(length = 32, nullable = false)
	public String city;

	@Size(min = 1, max = 10)
	@Column(name = "postal_code", length = 10, nullable = false)
	public String postalCode;

	@NotNull
	@Size(min = 2, max = 32)
	@Column(length = 32, nullable = false)
	public String country;
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "employee_id")
	@JsonIgnore
	public Employee employee;
	
	// @OneToOne(fetch = FetchType.LAZY)
	// @MapsId
	// @JoinColumn(name = "contact_person_id")
	// public ContactPerson contactPerson;

	// public void setEmployee(Employee employee) {
	// 	this.employee = employee;
	// 	this.id = employee.id;
	// }
}

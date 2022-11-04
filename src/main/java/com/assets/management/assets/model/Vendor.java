package com.assets.management.assets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "vendors")
public class Vendor extends PanacheEntity {

	@NotNull
	@Column(name = "company_name", length = 100, nullable = false)
	public String companyName;

	@NotNull
	@Column(length = 100, nullable = false)
	public String service;

	@NotNull
	@Column(name = "contact_person", length = 100, nullable = false)
	public String contactPerson;

	@NotNull
	@Column(name = "escalating_person", length = 100, nullable = false)
	public String escalatingPerson;

//	public Vendor(
//	        String companyName, String service, String contactPerson,
//	        String escalatingPerson
//	) {
//		this.companyName = companyName;
//		this.service = service;
//		this.contactPerson = contactPerson;
//		this.escalatingPerson = escalatingPerson;
//	}
}

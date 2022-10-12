package com.assets.management.electronic.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
	@Column(name = "contact_person", length = 100, nullable = false)
	public String contactPerson;

	@NotNull
	@Column(name = "escalation_person", length = 100, nullable = false)
	public String escalationPerson;

//	@OneToMany(
//	        mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true
//	) 
//	public List<SmartPhone> phones = new ArrayList<>();
//
//	public void addPhone(SmartPhone phone) {
//		phones.add(phone);
//		phone.setVendor(this);
//	}
//
//	public void removePhone(SmartPhone phone) {
//		phones.remove(phone);
//		phone.setVendor(null);
//	}
}

package com.assets.management.assets.model;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "contact_persons")
public class ContactPerson extends Person {

	@NotNull
	@Column(length = 64, nullable = false)
	public String title;
 
	@ManyToOne(fetch = FetchType.LAZY)
	public Supplier supplier;
}
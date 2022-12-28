package com.assets.management.assets.model;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "contact_persons")
public class ContactPerson extends Person {

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\\\p{L} .'-]+$")
	@Column(length = 64, nullable = false)
	public String position;
	
	@Column(name = "office_extension")
	public String extension;
 
	@ManyToOne(fetch = FetchType.LAZY)
	public Supplier supplier;
}

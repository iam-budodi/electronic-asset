package com.assets.management.assets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "addresses")
public class Address extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(name = "street_name", length = 32, nullable = false)
	public String street;
	
	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(name = "ward_name", length = 32, nullable = false)
	public String ward;

	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(name = "district_name", length = 32, nullable = false)
	public String district;
	
	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(length = 32, nullable = false)
	public String city;

	@Size(min = 1, max = 10)
	@Pattern(regexp = "^\\d{5}$", message = "should include only five digits number")
	@Column(name = "postal_code", length = 10, nullable = false)
	public String postalCode;

	@NotNull
	@Size(min = 2, max = 32)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(length = 32, nullable = false)
	public String country;
	
//	@MapsId
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "employee_fk", 
			foreignKey = @ForeignKey(
					name = "employee_address_fk_constraint", 
					foreignKeyDefinition = ""))
	public Employee employee; 
	
//	@MapsId
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id", 
			foreignKey = @ForeignKey(
					name = "supplier_address_fk_constraint", 
					foreignKeyDefinition = ""))
	public Supplier supplier; 
}

package com.assets.management.assets.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "suppliers")
public class Supplier extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(name = "company_name", length = 64, nullable = false)
	public String name;
	
	@NotNull
	@Email
	@Pattern(regexp = "^(?=.{1,64}@)[\\\\p{L}0-9_-]+(\\\\.[\\\\p{L}0-9_-]+)*@[^-][\\\\p{L}0-9-]+(\\\\.[\\\\p{L}0-9-]+)*(\\\\.[\\\\p{L}]{2,})$", message = "one or more character in not valid for proper email")
	@Column(name = "company_email", nullable = false)
	public String email;
	
	@NotNull
	@Column(name = "company_phone", length = 64, nullable = false)
	public String phone;
	
	@NotNull
	@Column(name = "company_website", length = 64, nullable = false)
	public String website;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "company_type", nullable = false)
	public SupplierType supplierType;

	@NotNull
	@Column(length = 500, nullable = false)
	@Pattern(regexp = "^[\\\\p{L} .'-?!;,]+$", message = "should include only letters, ' , ?, !, ; and - special characters")
	public String description;

	@CreationTimestamp
	@Column(name = "registered_at")
	public LocalDate registeredAt;
	

	@UpdateTimestamp
	@Column(name = "updated_at")
	public LocalDate updatedAt;

	@NotNull
	@Column(name = "registered_by", length = 64, nullable = false)
	@Pattern(regexp = "^[\\\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
	public String registeredBy;

	@Column(name = "updated_by")
	@Pattern(regexp = "^[\\\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
	public String updatedBy;
}

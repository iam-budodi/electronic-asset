package com.assets.management.assets.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.quarkus.panache.common.Parameters;

@Entity
@Table(
		name = "suppliers", 
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uniqueEmailandPhone", 
						columnNames = { "company_email", "company_phone" }) 
		})
@NamedQueries({
	@NamedQuery(
			name = "Supplier.getEmailOrPhone", 
			query = "FROM Supplier WHERE email = :email OR phone = :phone")
})
public class Supplier extends BaseEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(name = "company_name", length = 64, nullable = false)
	public String name;

	@NotNull
	@Email
	@Pattern(
			regexp = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$", 
			message = "one or more character in not valid for proper email")
	@Column(name = "company_email", nullable = false)
	public String email;

	// ^(((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3}$
	@NotNull
	@Pattern(
			regexp = "^[((((\\+)?\\(\\d{3}\\)[- ]?\\d{3})|\\d{4})[- ]?\\d{3}[- ]?\\d{3})]{10,18}$", 
			message = "must any of the following format (255) 744 608 510, (255) 744 608-510, (255) 744-608-510, (255)-744-608-510, "
			+ "+(255)-744-608-510, 0744 608 510, 0744-608-510, 0744608510 and length btn 10 to 18 characters including space")
	@Column(name = "company_phone", length = 18, nullable = false)
	public String phone;

	// @NotNull
	@Column(name = "company_website")
	public String website;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "company_type", nullable = false)
	public SupplierType supplierType;

	@NotNull
	@Column(length = 500, nullable = false)
	@Pattern(
			regexp = "^[\\p{L} .'-?!;,]+$", 
			message = "should include only letters, ' , ?, !, ; and - special characters")
	public String description;
	
	@OneToOne(
			mappedBy = "supplier", 
			orphanRemoval = true,
			cascade = CascadeType.ALL, 
			fetch = FetchType.LAZY)
	public Address address;
	
	public static boolean checkByEmailAndPhone(String email, String phone) {
		return find(
				"#Supplier.getEmailOrPhone", 
				Parameters.with("email", email).and("phone", phone).map())
				.firstResultOptional()
				.isPresent();
	}
}

package com.assets.management.assets.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public class Person extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
	@Column(name = "first_name", length = 64, nullable = false)
	public String firstName;

	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
	@Column(name = "middle_name", length = 64)
	public String middleName;

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\\\p{L} .'-]+$", message = "should include only letters ' and - special characters")
	@Column(name = "last_name", length = 64)
	public String lastName;

	@Enumerated(EnumType.STRING)
	public Gender gender;

	@NotNull
	@Pattern(regexp = "^(\\\\+\\\\d{1,3}( )?)?((\\\\(\\\\d{3,4}\\\\))|\\\\d{3})[-]?\\\\d{3}[-]?\\\\d{3}$", message = "must any of the following format (255) 744 608 510, (255) 744 608-510, (255) 744-608-510, (255)-744-608-510, 0744 608 510, 0744-608-510, 0744608510")
	@Column(name = "phone_number")
	public String mobile;

	@NotNull
	@Email
	@Column
	@Pattern(regexp = "^(?=.{1,64}@)[\\\\p{L}0-9_-]+(\\\\.[\\\\p{L}0-9_-]+)*@[^-][\\\\p{L}0-9-]+(\\\\.[\\\\p{L}0-9-]+)*(\\\\.[\\\\p{L}]{2,})$", message = "one or more character in not valid for proper email")
	public String email;

	@CreationTimestamp
	@Column(name = "created_at")
	public LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	public LocalDateTime updatedAt;

	@Column(name = "created_by")
	public String createdBy;

	@Column(name = "updated_by")
	public String updatedBy;

}

package com.assets.management.assets.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public class Person extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Column(name = "first_name", length = 64, nullable = false)
	public String firstName;

	@Size(min = 2, max = 64)
	@Column(name = "middle_name", length = 64)
	public String middleName;

	@NotNull
	@Size(min = 2, max = 64) 
	@Column(name = "last_name", length = 64)
	public String lastName;
	
	@Enumerated(EnumType.STRING)
	public Gender gender; 

	@NotNull
	@Column(name = "phone_number")
	public String mobile;
	
	@NotNull
	@Column
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

package com.assets.management.assets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "departments")
public class Department extends PanacheEntity {

	@NotNull
	@Column(name = "department_name", length = 64, nullable = false)
	public String name;

	@Column(length = 4000)
	public String description;
}

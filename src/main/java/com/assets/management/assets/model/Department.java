package com.assets.management.assets.model;

import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

@Entity
@Table(name = "departments")
public class Department extends PanacheEntity {

	@NotNull
	@Column(name = "department_name", length = 64, nullable = false)
	public String name;

	@Column(length = 4000)
	public String description;

	public static List<Department> findAllOrderByName() {
		return listAll(Sort.by("name"));
	}

	public static Optional<Department> findByName(String name) {
		return find("LOWER(name)", name.toLowerCase())
				.firstResultOptional();
	}
}

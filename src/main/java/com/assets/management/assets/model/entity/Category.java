package com.assets.management.assets.model.entity;

import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

@Entity
@Table(name = "categories")
@Schema(description = "Category representation")
public class Category extends PanacheEntity {

	@NotNull
	@Schema(required = true)
	@Column(name = "category_name", length = 64, nullable = false)
	public String name;

	@Column(length = 1000)
	public String description;

	public static List<Category> findAllOrderByName() {
		return listAll(Sort.by("name"));
	}

	public static Optional<Category> findByName(String name) {
		return find("LOWER(name)", name.toLowerCase()).firstResultOptional();
	}
}

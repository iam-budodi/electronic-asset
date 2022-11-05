package com.assets.management.assets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "categories") 
public class Category extends PanacheEntity {

	@NotNull
	@Column(name = "category_name", length = 64, nullable = false)
	public String name;
	 
	@Column(length = 1000)
	public String description;
}

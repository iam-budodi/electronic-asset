package com.assets.management.assets.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "suppliers")
public class Supplier extends PanacheEntity {

	@NotNull
	@Column(length = 64, nullable = false)
	public String title;

	@NotNull
	@Column(length = 500, nullable = false)
	public String summary;

	@NotNull
	@Column(name = "registered_at")
	public LocalDate registeredAt;

	@NotNull
	@Column(name = "registered_by", length = 64, nullable = false)
	public String registeredBy;

}

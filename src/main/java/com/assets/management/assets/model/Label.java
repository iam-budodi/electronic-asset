package com.assets.management.assets.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "labels")
public class Label extends PanacheEntity {

	@Size(max = 64)
	@Column(name = "item_tag", length = 64)
	public String itemTag;

	@NotNull
	@Size(max = 4000)
	@Column(name = "item_qr_string", length = 4000, nullable = false)
	public byte[] itemQrString;
	
	@OneToOne(
			mappedBy = "label", 
			orphanRemoval = true,
			cascade = CascadeType.ALL, 
			fetch = FetchType.LAZY)
	public Computer computer;
	 
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "assignment_id")
	@JsonIgnore
	public ItemAssignment itemAssignment;
}

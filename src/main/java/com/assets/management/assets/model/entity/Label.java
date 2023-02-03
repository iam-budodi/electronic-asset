package com.assets.management.assets.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "labels")
public class Label extends PanacheEntity {
	
// TO MOVED TO THE BARCODE TABLE
//	@Size(max = 64)
//	@Column(name = "item_tag", length = 64)
//	public String itemTag;

	@NotNull
	@Size(max = 4000)
	@Column(name = "asset_qr_string", length = 4000, nullable = false)
	public byte[] qrByteString;
	
//	 TO BE DELETED  PERMANENTLY
//	@OneToOne(fetch = FetchType.LAZY)
//	@MapsId
//	@JoinColumn(name = "assignment_id")
//	@JsonIgnore
//	public ItemAssignment itemAssignment;
}

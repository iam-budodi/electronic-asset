package com.assets.management.assets.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(
		name = "qr_codes", 
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_label", 
						columnNames = { "qr_label" })})
public class QRCode extends PanacheEntity {

	@NotNull
	@Size(max = 4000)
	@Column(name = "qr_label", length = 4000, nullable = false)
	public byte[] qrByteString;
	
}

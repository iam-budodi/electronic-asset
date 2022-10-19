package com.assets.management.assets.model;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.constraint.NotNull;

@Entity
@Table(name = "assets")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Asset extends PanacheEntity {

	@NotNull
	@Column(name = "model_name", length = 100, nullable = false)
	public String modelName;

	@NotNull
	@Column(name = "model_number", length = 100, nullable = false)
	public String modelNumber;

	@NotNull
	@Column(name = "serial_number", length = 100, nullable = false)
	public String serialNumber;

	@NotNull
	@Column(name = "stocked_at")
	public Instant stockedAt;

	@Column(name = "updated_at")
	public Instant updatedAt;

	@Column(name = "employ_date")
	public Instant employDate;

	@Column(name = "asset_tag", length = 100)
	public String assetTag;

	@Column(name = "asset_name", length = 100)
	public String assetName;

	@Column(name = "topup_amount")
	public BigDecimal topupAmout;

	@Enumerated(EnumType.STRING)
	public Status status;

	@Column(name = "qr_code_string", length = 4000)
	public String qrString;

	@ManyToOne(fetch = FetchType.LAZY)
	public EndUser endUser;

	@ManyToOne(fetch = FetchType.LAZY)
	public Vendor vendor;

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object asset) {
		if (this == asset)
			return true;
		if (!(asset instanceof Asset))
			return false;
		return id != null && id.equals(((Asset) asset).id);
	}

}

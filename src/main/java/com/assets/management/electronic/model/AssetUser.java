package com.assets.management.electronic.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class AssetUser extends Employee {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "device_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public ElectronicDevice device;

	public void setElectronicDevice(ElectronicDevice device) {
		this.device = device;
	}
}

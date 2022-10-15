package com.assets.management.electronic.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity 
public class Computer extends ElectronicDevice {
//public class Computer {


	@Column(name = "computer_name", length = 100)
	public String assetName;
	
	@Column(name = "asset_tag", length = 100)
	public String assetTag;
 
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vendor_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Vendor vendor;

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
	
//	public String    brand;
//	public String    name;
//	public String    comment;
//	public Instant   manufacturedDate;
//	public String    manufacturer;
//	public LocalDate commissionedDate;
//	public String    serialNumber;

//	@OneToOne(
//			mappedBy = "computer", cascade = CascadeType.ALL,
//			fetch = FetchType.LAZY, optional = false, orphanRemoval = true
//	)
//	@PrimaryKeyJoinColumn
//	public QRCode code;
//
//	public void addCode(QRCode code) {
//		this.code = code;
//		code.setComputer(this);
//	}
//
//	public void removeCode() {
//		code.setComputer(null);
//		this.code = null;
//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n{");
		builder.append("\nSN: ").append(serialNumber).append("\t");
		builder.append("\nBRAND: ").append(brand).append("\t"); 
//		builder.append("\nCOMMISSIONED DATE: ").append(
//				commissionedDate.equals(null) ? "Still in stock"
//						: commissionedDate
//		).append("\t"); 
		builder.append("\n}");
		return builder.toString();
	}

}

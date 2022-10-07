package com.assets.management.electronic.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "computers")
public class Computer extends Device {
//public class Computer {

	@NotNull
	public Boolean mouse;
//	public String    brand;
//	public String    name;
//	public String    comment;
//	public Instant   manufacturedDate;
//	public String    manufacturer;
//	public LocalDate commissionedDate;
//	public String    serialNumber;

	@OneToOne(
			mappedBy = "computer", cascade = CascadeType.ALL,
			fetch = FetchType.LAZY, optional = false, orphanRemoval = true
	)
	@PrimaryKeyJoinColumn
	public QRCode code;

	public void addCode(QRCode code) {
		this.code = code;
		code.setComputer(this);
	}

	public void removeCode() {
		code.setComputer(null);
		this.code = null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n{");
		builder.append("\nSN: ").append(serialNumber).append("\t");
		builder.append("\nBRAND: ").append(brand).append("\t");
		builder.append("\nMANUFACTURER: ").append(manufacturer).append("\t");
//		builder.append("\nCOMMISSIONED DATE: ").append(
//				commissionedDate.equals(null) ? "Still in stock"
//						: commissionedDate
//		).append("\t");
		builder.append("\nMOUSE: ").append(mouse ? "Yes" : "No");
		builder.append("\n}");
		return builder.toString();
	}

}

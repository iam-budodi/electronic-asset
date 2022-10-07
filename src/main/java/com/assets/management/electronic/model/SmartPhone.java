package com.assets.management.electronic.model;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "smart_phones")
//@DiscriminatorValue("S")
public class SmartPhone extends Device {

	@NotNull
	@Column(name = "topped_up")
	public Boolean toppedUp;

	@Column(name = "topup_amount")
	public BigDecimal topupAmout;

	@NotNull
	public Boolean paid;

	// Baeldung
//	@OneToOne(cascade = CascadeType.ALL)
//	@JoinColumn(name = "qrcode_id", referencedColumnName = "id")
//	public QRCode code;

	// Vlad
	@OneToOne(
			mappedBy = "phone", cascade = CascadeType.ALL,
			fetch = FetchType.LAZY, optional = false, orphanRemoval = true
	)
	@PrimaryKeyJoinColumn
	public QRCode code;

	public void addCode(QRCode code) {
		this.code = code;
		code.setPhone(this);
	}

	public void removeCode() {
		code.setPhone(null);
		this.code = null;
	} 
	
	@Override
	public String toString() {
		return "SmartPhone [toppedUp=" + toppedUp + ", topupAmout="
				+ topupAmout + ", paid=" + paid + ", code=" + code + ", name="
				+ name + ", brand=" + brand + ", serialNumber=" + serialNumber
				+ ", manufacturer=" + manufacturer + ", manufacturedDate="
				+ manufacturedDate + ", generatedAt=" + generatedAt
				+ ", commissionedDate=" + commissionedDate + ", qrString="
				+ qrString + ", comment=" + comment + ", status=" + status
				+ ", timeInUse=" + timeInUse + ", id=" + id + "]";
	}

//	public void addQR(QRCode code) {
//		if (code == null) {
//			if (this.code != null) {
//				this.code.setPhone(null);
//			}
//		} else {
//
//			code.setPhone(this);
//		}
//	}

//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("\n{");
//		builder.append("\nSN: ").append(serialNumber).append("\t");
////		builder.append("\nSTOCKED: ").append(stockedAt).append("\t");
//		builder.append("\nYTD: ").append(timeInUse).append("\t");
//		builder.append("\nBRAND: ").append(brand).append("\t");
////		builder.append("\nCOMMISSIONED DATE: ").append(
////				commissionedDate.equals(null) ? "Still in stock"
////						: commissionedDate
////		).append("\t");
//		builder.append("\nTOPPED UP: ").append(toppedUp ? "Yes" : "No");
//		builder.append("\nPAID: ").append(paid ? "Yes" : "No");
//		builder.append("\n}");
//		return builder.toString();
//	}
	
	
}

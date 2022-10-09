package com.assets.management.electronic.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity 
@Table(name = "qr_code")
public class QRCode extends PanacheEntity {
  
	@Column(name = "qr_string", length = 4000)
	public String qrString;
	
//	public Custodian custodian; // put it on the device
	
//	@OneToOne(fetch = FetchType.LAZY) // this is needed for bidirectional r/ship
//	@MapsId
//	@JoinColumn(name = "phone_id")
//	public SmartPhone phone;
//	
//	public void setPhone(SmartPhone phone) {
//		this.phone = phone;
//	}
//	
//	@OneToOne(fetch = FetchType.LAZY, optional = true) // this is needed for bidirectional r/ship
//	@MapsId
//	@JoinColumn(name = "computer_id", nullable = true)
//	public Computer computer;
// 
//	public void setComputer(Computer computer) {
//		this.computer = computer;
//	}
  
}

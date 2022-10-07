package com.assets.management.electronic.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity 
@Table(name = "qr_code")
public class QRCode extends PanacheEntity {
  
	@Column(name = "qr_string", length = 4000)
	public String qrString;
	
//	public Custodian custodian; // put it on the device
	
	@OneToOne(fetch = FetchType.LAZY) // this is needed for bidirectional r/ship
	@MapsId
	@JoinColumn(name = "phone_id")
	public SmartPhone phone;
	
	public void setPhone(SmartPhone phone) {
		this.phone = phone;
	}
	
	@OneToOne(fetch = FetchType.LAZY) // this is needed for bidirectional r/ship
	@MapsId
	@JoinColumn(name = "computer_id")
	public Computer computer;
 
	public void setComputer(Computer computer) {
		this.computer = computer;
	}
  
}

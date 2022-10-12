package com.assets.management.electronic.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.constraint.NotNull;

 
@Entity
@Table(name = "electronic_devices")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
public class ElectronicDevice extends PanacheEntity {
 
	@NotNull
	@Column(length = 100, nullable = false)
	public String brand;

	@NotNull
	@Column(name = "serial_number", length = 100, nullable = false)
	public String serialNumber;

	@Column(name = "acquisition_date")
	public LocalDate acquisitionDate; 
	
	@NotNull
	@Column(name = "stocked_at")
	public Instant generatedAt;

	@Column(name = "updated_at")
	public Instant updatedAt;

//	@NotNull
	@Column(name = "commissioning_date")
	public Instant commissionedDate;

	@Enumerated(EnumType.STRING)
	public Status status;

	@Column(name = "time_in_use")
	public Integer timeInUse; 
	
	@Column(name = "qr_string", length = 4000)
	public String qrString;
	
 
//	@PrePersist
//	@PreUpdate
//	protected void retire() {
//		if (commissionedDate == null) {
//			status = Status.Available;
//			return;
//		}
//
//		LocalDate date    = LocalDateTime
//    						.ofInstant(commissionedDate, ZoneOffset.UTC)
//    						.toLocalDate();
//		
//		LocalDate current = LocalDateTime
//		        			.ofInstant(Instant.now(), ZoneOffset.UTC)
//		        			.toLocalDate();
//		
//		timeInUse = Period
//				.between(date, current)
//				.getYears();
//		
//		if (timeInUse >= 5) status = Status.Retired;
//	}
}

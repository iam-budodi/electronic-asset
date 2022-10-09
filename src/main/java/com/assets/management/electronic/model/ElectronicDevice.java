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
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.constraint.NotNull;

//@MappedSuperclass

@Entity
@Table(name = "electronic_devices")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@NamedQueries(
//		value = { @NamedQuery(
//				name = "Device.sortCountByStatus",
//				query = "select status, count(*) total from Device d where d.status = :status group by status, total"
//		) }
//)
public class ElectronicDevice extends PanacheEntity {

	@Column(length = 100)
	public String name;

	@NotNull
	@Column(length = 100, nullable = false)
	public String brand;

	@NotNull
	@Column(name = "serial_number", length = 100, nullable = false)
	public String serialNumber;

	@Column(length = 100)
	public String manufacturer;

	@Column(name = "manufactured_date")
	public LocalDate manufacturedDate;

	@NotNull
	@Column(name = "stocked_at")
	public Instant generatedAt;
	
	@Column(name = "updated_at")
	public Instant updatedAt;

//	@NotNull
	@Column(name = "commissioning_date")
	public Instant commissionedDate; 

	@Column(length = 3000)
	public String comment; 

	@Enumerated(EnumType.STRING)
	public Status status;
 
	@Column(name = "qr_string", length = 4000)
	public String qrString;

	@Transient
	public Integer timeInUse;

	@PostLoad
	@PostPersist
	@PostUpdate
	protected void calculateAge() {
		if (commissionedDate == null) {
			timeInUse = null;
			return;
		}

		LocalDate date = LocalDateTime.ofInstant(
				commissionedDate, ZoneOffset.UTC
		).toLocalDate();
		LocalDate current = LocalDateTime.ofInstant(
				Instant.now(), ZoneOffset.UTC
		).toLocalDate();
		timeInUse = Period.between(date, current).getYears();
	}
}

package com.assets.management.assets.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "item_assignments")
public class ItemAssignment extends PanacheEntity {

	@NotNull
	@Column(name = "item_serial_number", length = 32, nullable = false)
	public String itemSerialNumber;

	@NotNull
	@Min(1)
	@Column(name = "quantity_assigned", nullable = false)
	public Integer qtyAssigned;
 
	@CreationTimestamp
	@Column(name = "date_assigned", nullable = false)
	public LocalDateTime dateAssigned;

	@Column(length = 4000)
	public String remarks;

	@ManyToOne(fetch = FetchType.LAZY)
	public Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	public Employee employee;
	 
	@OneToOne(
	        mappedBy = "itemAssignment", 
	        cascade = CascadeType.ALL,
	        fetch = FetchType.LAZY
	)
	public Label label;

	public static Boolean checkIfAssigned(Long itemId) {
		return find("item.id = ?1", itemId)
				.firstResultOptional()
				.isPresent();
	}
	public static Boolean checkIfExists(Long empId) {
		return find("employee.id = ?1", empId)
				.firstResultOptional()
				.isPresent();
	}

	public static QrContent projectQrContents(String sn) {
		// Query projection
		return find("itemSerialNumber", sn).project(QrContent.class).singleResult();
	}
}

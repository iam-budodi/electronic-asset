package com.assets.management.assets.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "transfer_history")
public class TransferHistory extends PanacheEntity {

	// change these two to manytoone and string to Employee
	// also add transfer count column to an Item
	// then add item serialNo and label r/ship
	// dont forget the projection method
//	@NotNull
//	@Size(min = 2, max = 64)
//	@Column(name = "transfer_from", length = 64, nullable = false)
//	public String transferFrom;
//
//	@NotNull
//	@Size(min = 2, max = 64)
//	@Column(name = "transfer_to", length = 64, nullable = false)
//	public String transferTo;

	@NotNull 
	@Min(1)
	@Column(name = "quantity", nullable = false)
	public Integer qty;

	@CreationTimestamp
	@Column(name = "date_transfered", nullable = false)
	public LocalDateTime dateTransfered;

	@Column(length = 4000)
	public String remarks;

	@ManyToOne(fetch = FetchType.LAZY)
	public Item item;
}

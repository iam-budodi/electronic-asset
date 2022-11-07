package com.assets.management.assets.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "transfer_history")
public class TransferHistory extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Column(name = "transfer_from", length = 64, nullable = false)
	public String transferFrom;

	@NotNull
	@Size(min = 2, max = 64)
	@Column(name = "transfer_to", length = 64, nullable = false)
	public String transferTo;

	@NotNull 
	@Min(1)
	@Column(name = "quantity", nullable = false)
	public Integer qty;

	@NotNull 
	@Column(name = "date_transfered", nullable = false)
	public LocalDate dateTransfered;

	@Column(length = 4000)
	public String remarks;

	@ManyToOne(fetch = FetchType.LAZY)
	public Item item;
}

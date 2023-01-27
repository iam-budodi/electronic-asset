package com.assets.management.assets.model.entity;
 
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "transfer_history")
public class TransferHistory extends PanacheEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	//@Column(name = "transfer_from_employee")
	public Employee transferedFromEmployee;

	@ManyToOne(fetch = FetchType.LAZY)
//	@Column(name = "transfer_to_employee")
	public Employee transferedToEmployee;

	@CreationTimestamp
	@Column(name = "date_transfered", nullable = false)
	public LocalDateTime dateTransfered;

	@NotNull 
	@Column(length = 4000)
	public String remarks;

	@ManyToOne(fetch = FetchType.LAZY)
	public Item item;
}

package com.assets.management.assets.model;

import java.time.LocalDate;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

// TODO: define entity and columns
public class TransferHistory extends PanacheEntity {

	public String transferFrom;
	public String transferTo;
	public Integer qty;
	public LocalDate dateTransfered;
	public String remarks;

	@ManyToOne(fetch = FetchType.LAZY)
	public Item item;
}

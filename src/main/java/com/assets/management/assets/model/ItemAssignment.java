package com.assets.management.assets.model;

import java.time.LocalDate;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

public class ItemAssignment extends PanacheEntity {

	public LocalDate dateAssigned;
	public String condition;
	public Integer qty;
	public String remarks;
	

	@ManyToOne(fetch = FetchType.LAZY)
	public Item item;
	

	@ManyToOne(fetch = FetchType.LAZY)
	public Employee employee;
}

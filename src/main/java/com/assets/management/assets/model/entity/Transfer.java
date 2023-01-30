package com.assets.management.assets.model.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "asset_transfers")
public class Transfer extends PanacheEntity {

	@CreationTimestamp
	@Column(name = "transfer_date", nullable = false)
	public Instant transferDate;

	@Column(name = "transfer_remarks", length = 4000)
	public String transferRemark; 

	@JoinColumn(
			name = "prev_employee_fk", 
			foreignKey = @ForeignKey(
					name = "prev_employee_transfer_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Employee fromEmployee; 

	@JoinColumn(
			name = "new_employee_fk", 
			foreignKey = @ForeignKey(
					name = "new_employee_transfer_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Employee toEmployee; 

	@JoinColumn(
			name = "asset_fk", 
			foreignKey = @ForeignKey(
					name = "asset_transfer_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Asset asset; 
}

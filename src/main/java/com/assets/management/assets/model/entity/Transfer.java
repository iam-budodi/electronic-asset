package com.assets.management.assets.model.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.assets.management.assets.model.valueobject.AllocationStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "asset_transfers")
public class Transfer extends PanacheEntity {

	@CreationTimestamp
	@Column(name = "transfer_date", nullable = false)
	public Instant transferDate;

	@Column(name = "transfer_remarks", length = 4000)
	public String transferRemark; 
	
	@ElementCollection
	@Enumerated(EnumType.STRING)
	@ColumnDefault(value = "'TRANSFERED'") 
	@JoinColumn(
			name = "transfer_status",
			nullable = false,
			foreignKey = @ForeignKey(
					name = "transfer_status_fk_constraint", 
					foreignKeyDefinition = ""))
//	public Set<AllocationStatus> status = new HashSet<>(List.of(AllocationStatus.ALLOCATED));
	public Set<AllocationStatus> status = new HashSet<>();

	@JoinColumn(
			name = "from_employee_fk", 
			foreignKey = @ForeignKey(
					name = "fransfer_from_employee_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Employee fromEmployee; 

	@JoinColumn(
			name = "to_employee_fk", 
			foreignKey = @ForeignKey(
					name = "transfer_to_employee_fk_constraint", 
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

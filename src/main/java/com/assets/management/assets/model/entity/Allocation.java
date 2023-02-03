package com.assets.management.assets.model.entity;

import java.time.Instant;

import javax.persistence.Column;
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
import org.hibernate.annotations.DynamicInsert;

import com.assets.management.assets.model.valueobject.AllocationStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@DynamicInsert
@Table(name = "asset_allocations")
public class Allocation extends PanacheEntity {

	@CreationTimestamp
	@Column(name = "allocation_date", nullable = false)
	public Instant allocationDate; 

	@Column(name = "deallocation_date")
	public Instant deallocationDate; 

	@Column(name = "allocation_remarks", length = 4000)
	public String allocationRemark; 
	
//	@ElementCollection
	@Enumerated(EnumType.STRING)
	@ColumnDefault(value = "'ALLOCATED'") 
//	@ColumnDefault(value = AllocationStatus.ALLOCATED)
	@JoinColumn(
			name = "allocation_status",
			nullable = false,
			foreignKey = @ForeignKey(
					name = "allocation_status_fk_constraint", 
					foreignKeyDefinition = ""))
//	public Set<AllocationStatus> status = new HashSet<>(List.of(AllocationStatus.ALLOCATED));
	public AllocationStatus status;

	@JoinColumn(
			name = "employee_fk", 
			foreignKey = @ForeignKey(
					name = "employee_allocation_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Employee employee; 

	@JoinColumn(
			name = "asset_fk", 
			foreignKey = @ForeignKey(
					name = "asset_allocation_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Asset asset; 

}

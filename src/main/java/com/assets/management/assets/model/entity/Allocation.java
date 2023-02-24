package com.assets.management.assets.model.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;

import com.assets.management.assets.model.valueobject.AllocationStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;

@Entity
// @DynamicInsert
@Table(name = "asset_allocations")
@NamedQueries({
	@NamedQuery(
			name = "Allocation.listAllandFilter", 
			query = " FROM Allocation a LEFT JOIN FETCH a.employee e LEFT JOIN FETCH e.department "
					+ "LEFT JOIN FETCH e.address LEFT JOIN FETCH a.asset ast LEFT JOIN FETCH ast.category "
					+ "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
					+ "LEFT JOIN FETCH s.address WHERE e.id = :employeeId AND (:assetId  IS NULL OR ast.id = :assetId) "
					+ "AND (:status IS NULL OR :status MEMBER OF a.status)"),
	@NamedQuery(
			name = "Allocation.qrPreview", 
			query = " FROM Allocation a LEFT JOIN FETCH a.employee e LEFT JOIN FETCH e.department "
					+ "LEFT JOIN FETCH e.address LEFT JOIN FETCH a.asset ast LEFT JOIN FETCH ast.category "
					+ "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
					+ "LEFT JOIN FETCH s.address WHERE e.id = :employeeId AND a.id = :allocationId")
})
@Schema(description = "Allocation representation")
public class Allocation extends PanacheEntity {

	@CreationTimestamp
	@Column(name = "allocation_date", nullable = false)
	public Instant allocationDate; 

	@Column(name = "deallocation_date")
	public Instant deallocationDate; 

	@Column(name = "allocation_remarks", length = 4000)
	public String allocationRemark; 
	
	@ElementCollection
	@Enumerated(EnumType.STRING)
	@JoinColumn(
			name = "allocation_status",
			nullable = false,
			foreignKey = @ForeignKey(
					name = "allocation_status_fk_constraint", 
					foreignKeyDefinition = ""))
	public Set<AllocationStatus> status = new HashSet<>(List.of(AllocationStatus.ALLOCATED));

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
	
	public static List<Allocation> listAllandFilterQuery(AllocationStatus filteredStatus, Long employeeId) {
		return find("#Allocation.listAllandFilter",
				Parameters.with("employeeId", employeeId).and("assetId", null)
				.and("status", filteredStatus))
				.list();
	}
	
	public static Allocation assetForQRPreview(Long employeeId, Long assetId) {
		return find("#Allocation.listAllandFilter",
				Parameters.with("employeeId", employeeId).and("assetId", assetId)
				.and("status", AllocationStatus.ALLOCATED))
				.firstResult();
	}
	
	public static Allocation qrPreviewDetails(Long employeeId, Long allocationId) {
		return find("#Allocation.qrPreview",
				Parameters.with("employeeId", employeeId).and("allocationId", allocationId))
				.firstResult();
	}
	
	@Override
	public String toString() {
		return "Allocation [allocationDate=" + allocationDate + ", deallocationDate=" + deallocationDate
				+ ", allocationRemark=" + allocationRemark + ", status=" + status + ", employee=" + employee
				+ ", asset=" + asset + "]";
	}
	
}

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

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.assets.management.assets.model.valueobject.AllocationStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;

@Entity
@Table(name = "asset_transfers")
@NamedQueries({
	@NamedQuery(
			name = "Transfer.listAllandFilter", 
			query = "FROM Transfer t LEFT JOIN FETCH t.fromEmployee fro LEFT JOIN FETCH fro.department "
					+ "LEFT JOIN FETCH fro.address LEFT JOIN FETCH t.toEmployee to LEFT JOIN FETCH to.department "
					+ "LEFT JOIN FETCH to.address LEFT JOIN FETCH t.asset  ast LEFT JOIN FETCH ast.category "
					+ "LEFT JOIN FETCH ast.label LEFT JOIN FETCH ast.purchase p  LEFT JOIN FETCH p.supplier s "
					+ "LEFT JOIN FETCH s.address WHERE to.id = :employeeId  AND (:assetId  IS NULL OR ast.id = :assetId) "
					+ "AND (:status IS NULL OR :status MEMBER OF t.status)")})
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
	
	public static List<Transfer> listAllandFilterQuery(AllocationStatus filteredStatus, Long employeeId) {
		return find("#Transfer.listAllandFilter",
				Parameters.with("employeeId", employeeId).and("assetId", null)
				.and("status", filteredStatus))
				.list();
	}
	
	public static Transfer assetForQRPreview(Long employeeId, Long assetId) {
		return find("#Transfer.listAllandFilter",
				Parameters.with("employeeId", employeeId).and("assetId", assetId)
				.and("status", AllocationStatus.ALLOCATED))
				.firstResult();
	}
	
	@Override
	public String toString() {
		return "Transfer [transferDate=" + transferDate + ", transferRemark=" + transferRemark + ", status=" + status
				+ ", fromEmployee=" + fromEmployee + ", toEmployee=" + toEmployee + ", asset=" + asset + "]";
	} 
	
}

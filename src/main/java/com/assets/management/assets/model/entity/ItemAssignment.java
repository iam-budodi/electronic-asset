package com.assets.management.assets.model.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.assets.management.assets.model.valueobject.AllocationStatus;
import com.assets.management.assets.model.valueobject.QrContent;
import com.assets.management.assets.model.valueobject.Status;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

//@Entity
@DynamicInsert
//@Table(name = "item_assignments")
public class ItemAssignment extends PanacheEntity {

    @NotNull
    @Column(name = "item_serial_number", length = 32, nullable = false)
    public String itemSerialNumber;

    @NotNull
    @Min(1)
    @Column(name = "quantity_assigned", nullable = false)
    public Integer qtyAssigned;

    @CreationTimestamp
    @Column(name = "date_assigned", nullable = false)
    public LocalDateTime dateAssigned;


    @ColumnDefault(value = "'Assigned'")
    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false)
    public AllocationStatus status;

    @Column(length = 4000)
    public String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    public Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    public Employee employee;
//	 
//	@OneToOne(
//	        mappedBy = "itemAssignment", 
//	        cascade = CascadeType.ALL,
//	        fetch = FetchType.LAZY
//	)
//	public Label label;

    public static Boolean isItemAssigned(Long itemId) {
        return find("item.id = ?1", itemId)
                .firstResultOptional()
                .isPresent();
    }

    public static Boolean isEmployeeExists(Long empId) {
        return find("employee.id = ?1", empId)
                .firstResultOptional()
                .isPresent();
    }

    public static PanacheQuery<PanacheEntityBase> hasItem(Long itemId) {
        return find(
                "item.id = ?1 AND item.status <> ?2",
                itemId, Status.Transfered);
    }

    public static QrContent projectQrContents(String sn) {
        // Query projection
        return find("itemSerialNumber", sn).project(QrContent.class).singleResult();
    }
}

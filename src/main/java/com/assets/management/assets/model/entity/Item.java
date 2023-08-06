package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.Status;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

//@Entity
@DynamicInsert
//@Table(name = "items")
public class Item extends PanacheEntity {

    @NotNull
    @Column(name = "item_name", length = 64, nullable = false)
    public String itemName;

    // moved to assignment and transfer
//	@NotNull
//	@Column(name = "serial_number", length = 32, nullable = false)
//	public String serialNumber;

    @NotNull
    @Min(1)
    @Column(name = "quantity", nullable = false)
    public Integer qtyBought;

    @ColumnDefault(value = "'New'")
//	@Generated(GenerationTime.INSERT) // substituted by @DynamicInsert
    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false)
    public Status status;

    @NotNull
    @Column(name = "date_purchased")
    public LocalDate datePurchased;

    @Column(name = "tranfer_count")
    public Integer transferCount;

    @Column(length = 1000)
    public String description;

    @ManyToOne(fetch = FetchType.LAZY)
    public Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    public Supplier supplier;

//  should be generated and mapped on the assignment and transfer table
//	@OneToOne(
//	        mappedBy = "item", 
//	        cascade = CascadeType.ALL,
//	        fetch = FetchType.LAZY
//	)
//	public Label label;
}

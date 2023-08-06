package com.assets.management.assets.model.entity;

import com.assets.management.assets.model.valueobject.Peripheral;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Entity
@DiscriminatorValue("COMPUTER")
@Schema(description = "Computer representation")
//@Table(
//		name = "computers", 
//		uniqueConstraints = {
//				@UniqueConstraint(
//						name = "unique_serial_number", 
//						columnNames = { "serial_number" })})
//@NamedQueries({
//	@NamedQuery(
//			name = "Computer.getSN", 
//			query = "FROM Computer WHERE serialNumber = :serialNumber")})
public class Computer extends Asset /*  extends PanacheEntity */ {
//
//	@NotNull
//	@Size(min = 2, max = 64)
//	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
//	@Column(name = "brand_name", length = 64, nullable = false)
//	public String brand;
//
//	@NotNull
//	@Size(min = 2, max = 64)
//	@Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "should include only letters, ' and - special characters")
//	@Column(name = "model_name", length = 64, nullable = false)
//	public String model;
//
//	@NotNull
//	@Column(name = "model_number", nullable = false)
//	public String modelNumber;
//	
//	@NotNull
//	@Column(name = "serial_number", nullable = false)
//	public String serialNumber;
//
//	@NotNull
//	@Column(nullable = false)
//	public String manufacturer;

    @NotNull
    @Schema(required = true)
    @Column(name = "computer_processor", nullable = false)
    public String processor;

    @NotNull
    @Schema(required = true)
    @Column(name = "computer_memory", nullable = false)
    public Integer memory;

    @NotNull
    @Schema(required = true)
    @Column(name = "computer_storage", nullable = false)
    public Integer storage;

    @Column(name = "operating_system")
    public String operatingSystem;

    @NotNull
    @Schema(required = true)
    @Column(name = "display_size", nullable = false)
    public Double displaySize;

    @Column(name = "graphics_card")
    public Integer graphicsCard;

    @NotNull
    @Schema(required = true)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @JoinColumn(
            name = "peripherals",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "device_peripherals_fk_constraint",
                    foreignKeyDefinition = ""))
    public Set<Peripheral> peripherals;

//	@JoinColumn(
//			name = "category_fk", 
//			foreignKey = @ForeignKey(
//					name = "device_category_fk_constraint", 
//					foreignKeyDefinition = ""))
//	@ManyToOne(fetch = FetchType.LAZY)
//	public Category category;
// 
////	@MapsId
////	@JsonIgnore
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(
//			name = "label_fk", 
//			foreignKey = @ForeignKey(
//					name = "computer_label_fk_constraint", 
//					foreignKeyDefinition = ""))
//	public Label label;
//
//	@NotNull
//	@JoinColumn(
//			name = "purchase_invoice_number", 
//			nullable = false,
//			referencedColumnName = "purchase_invoice_number",
//			foreignKey = @ForeignKey(
//					name = "device_purchase_fk_constraint", 
//					foreignKeyDefinition = ""))
//	@ManyToOne(fetch = FetchType.LAZY)
//	public Purchase purchase;
//
//	 
////	o	asset_warranty_expiration (foreign key to asset warranty table) //think about it //invoice_number
////	@Override
//	public static boolean checkSerialNumber(String serialNumber) {
//		return find(
//				"#Computer.getSN", Parameters.with("serialNumber", serialNumber).map())
//				.firstResultOptional().isPresent();
//	}
}

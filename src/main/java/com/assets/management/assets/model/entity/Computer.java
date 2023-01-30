package com.assets.management.assets.model.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotNull;

import com.assets.management.assets.model.valueobject.Peripheral;

@Entity
@DiscriminatorValue("COMPUTER")
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
public class Computer extends Asset /*  extends PanacheEntity */{
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
	@Column(name = "computer_processor", nullable = false)
	public String processor;

	@NotNull
	@Column(name = "computer_memory", nullable = false)
	public Integer memory;

	@NotNull
	@Column(name = "computer_storage", nullable = false)
	public Integer storage;

	@Column(name = "operating_system")
	public String operatingSystem;

	@NotNull
	@Column(name = "display_size", nullable = false)
	public Double displaySize;

	@Column(name = "graphics_card")
	public Integer graphicsCard;

	@NotNull
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

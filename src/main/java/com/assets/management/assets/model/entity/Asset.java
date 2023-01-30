package com.assets.management.assets.model.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;

@Entity
@Table(
		name = "assets", 
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_serial_number", 
						columnNames = { "serial_number" })})
@NamedQueries({
	@NamedQuery(
			name = "Asset.getSN", 
			query = "FROM Asset WHERE serialNumber = :serialNumber")})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "asset_discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ASSET")
public class Asset  extends PanacheEntity {

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
	@Column(name = "brand_name", length = 64, nullable = false)
	public String brand;

	@NotNull
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "should include only letters, ' and - special characters")
	@Column(name = "model_name", length = 64, nullable = false)
	public String model;

	@NotNull
	@Column(name = "model_number", nullable = false)
	public String modelNumber;
	
	@NotNull
	@Column(name = "serial_number", nullable = false)
	public String serialNumber;

	@NotNull
	@Column(nullable = false)
	public String manufacturer;
	
	@JoinColumn(
			name = "category_fk", 
			foreignKey = @ForeignKey(
					name = "asset_category_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Category category;
 
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "label_fk", 
			foreignKey = @ForeignKey(
					name = "computer_label_fk_constraint", 
					foreignKeyDefinition = ""))
	public Label label;

	@NotNull
	@JoinColumn(
			name = "purchase_invoice_number", 
			nullable = false,
			referencedColumnName = "purchase_invoice_number",
			foreignKey = @ForeignKey(
					name = "asset_purchase_fk_constraint", 
					foreignKeyDefinition = ""))
	@ManyToOne(fetch = FetchType.LAZY)
	public Purchase purchase;

//	o	asset_warranty_expiration (foreign key to asset warranty table) //think about it //invoice_number
//	@Override
	public static boolean checkSerialNumber(String serialNumber) {
		return find(
				"#Asset.getSN", Parameters.with("serialNumber", serialNumber).map())
				.firstResultOptional().isPresent();
	}
}

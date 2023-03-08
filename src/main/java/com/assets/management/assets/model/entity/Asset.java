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

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.assets.management.assets.model.valueobject.QrContent;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

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
@Schema(description = "Asset representation")
public class Asset  extends PanacheEntity {

	@NotNull
	@Schema(required = true)
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\p{L} .'-]+$", message = "should include only letters, ' and - special characters")
	@Column(name = "brand_name", length = 64, nullable = false)
	public String brand;

	@NotNull
	@Schema(required = true)
	@Size(min = 2, max = 64)
	@Pattern(regexp = "^[\\p{L}0-9 .'-]+$", message = "should include only letters, ' and - special characters")
	@Column(name = "model_name", length = 64, nullable = false)
	public String model;

	@NotNull
	@Schema(required = true)
	@Column(name = "model_number", nullable = false)
	public String modelNumber;
	
	@NotNull
	@Schema(required = true)
	@Column(name = "serial_number", nullable = false)
	public String serialNumber;

	@NotNull
	@Schema(required = true)
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
					name = "asset_label_fk_constraint", 
					foreignKeyDefinition = ""))
	public QRCode label;

	@NotNull
	@Schema(required = true)
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
	
	// method overloading
	public static PanacheQuery<Asset> retrieveAllOrById() {
		return retrieveAllOrById(null);
	}
	
	public static PanacheQuery<Asset> retrieveAllOrById(Long assetId) {
		return find("SELECT DISTINCT a FROM Asset a LEFT JOIN FETCH a.category LEFT JOIN FETCH a.label "
				+ "LEFT JOIN FETCH a.purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address "
				+ "WHERE (:assetId IS NULL OR a.id = :assetId) ",
				Sort.by("p.purchaseDate").and("a.brand").and("a.model"), 
				Parameters.with("assetId", assetId));
	}
	
	public static PanacheQuery<Asset> getAssetByInvoice(String invoiceNumber) {
		return find("FROM Asset a LEFT JOIN FETCH a.category cg LEFT JOIN FETCH a.label "
				+ "LEFT JOIN FETCH a.purchase p LEFT JOIN FETCH p.supplier s LEFT JOIN FETCH s.address "
				+ "WHERE p.invoiceNumber = :invoiceNumber", 
				Sort.by("p.purchaseDate").and("cg.name").and("a.brand"), 
				Parameters.with("invoiceNumber", invoiceNumber));
	}
	
	public static QrContent projectQrContents(String sn) {
		// Query projection
		return find("serialNumber", sn).project(QrContent.class).singleResult();
	}
}

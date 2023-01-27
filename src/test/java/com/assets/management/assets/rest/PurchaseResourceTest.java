package com.assets.management.assets.rest;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.entity.Address;
import com.assets.management.assets.model.entity.Computer;
import com.assets.management.assets.model.entity.Purchase;
import com.assets.management.assets.model.entity.Supplier;
import com.assets.management.assets.model.valueobject.PurchasePerSupplier;
import com.assets.management.assets.model.valueobject.SupplierType;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(PurchaseResource.class)
class PurchaseResourceTest {
	// PURCHASE
	private static final LocalDate PURCHASE_DATE = LocalDate.of(2023, Month.JANUARY, 19);
	private static final Integer PURCHASE_QUANTITY = 5;
	private static final BigDecimal PURCHASE_PRICE = BigDecimal.valueOf(1850000).setScale(2, RoundingMode.HALF_UP);
	private static final String INVOICE_NUMBER = "LP-123456CTY";
	
	// UPDATED 
	private static final LocalDate UPDATED_PURCHASE_DATE = LocalDate.of(2023, Month.JANUARY, 20);
	private static final Integer UPDATED_PURCHASE_QUANTITY = 6;
	private static final BigDecimal UPDATED_PURCHASE_PRICE = PURCHASE_PRICE;
	private static final String UPDATED_INVOICE_NUMBER = "LP-123456CTY - Updated";
	
	// SUPPLIER 
	private static final String NAME = "Laptop City";
	private static final String EMAIL = "laptop@laptopcity.co.tz";
	private static final String PHONE = "0744 111 700";
	private static final String REGISTERED_BY = "Japhet";
	private static final String DESCRIPTION = "Computers and accessories";
	
	// SUPPLIER ADDRESS 
	private static final String DEFAULT_STREET = "Mikocheni";
	private static final String DEFAULT_WARD = "Msasani";
	private static final String DEFAULT_DISTRICT = "Kinondoni";
	private static final String DEFAULT_CITY = "Dar es Salaam";
	private static final String DEFAULT_POSTAL_CODE = "14110";
	private static final String DEFAULT_COUNTRY = "Tanzania";
	
	private static Supplier supplier;
	private static String purchaseId;
	private static String supplierId;
	
	@TestHTTPResource
	@TestHTTPEndpoint(SupplierResource.class)
	static URL supplierURL;
	
	@TestHTTPResource("computers")
	@TestHTTPEndpoint(PurchaseResource.class)
	URL computersEndpoint;
	
	@TestHTTPResource("count")
	@TestHTTPEndpoint(PurchaseResource.class)
	URL countEndpoint;
	
	@Test
	@Order(1)
	void shouldListEmptyPurchases() {
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get()
			.then()
			 	.statusCode(Status.OK.getStatusCode())
			 	.body("isEmpty()", is(true))
				.contentType(APPLICATION_JSON); 
	}
		
	@Test
	@Order(2)
	void shouldNotFindRandomPurchase() {
		Long randomId = new Random().nextLong();
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", randomId)
			.when()
			.get("/{id}")
			.then()
			 	.statusCode(NOT_FOUND.getStatusCode())
				.contentType(APPLICATION_JSON);
	}

	@Test
	@Order(3)
	void shouldNotCreateInvalidPurchase() {
		final Purchase purchase = new Purchase();
			given() 
				.body(purchase)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
					.statusCode(BAD_REQUEST.getStatusCode());
	}
	
	@Test 
	@Order(4)
	void ShouldCreateSupplier() {
		Address address = new Address();
		address.street = DEFAULT_STREET;
		address.ward = DEFAULT_WARD;
		address.district = DEFAULT_DISTRICT;
		address.city = DEFAULT_CITY;
		address.postalCode = DEFAULT_POSTAL_CODE;
		address.country = DEFAULT_COUNTRY;
		
		Supplier supplier = new Supplier();
		supplier.name = NAME;
		supplier.email = EMAIL;
		supplier.phone = PHONE;
		supplier.registeredBy = REGISTERED_BY;
		supplier.supplierType = SupplierType.RETAILER;
		supplier.description = DESCRIPTION;
		supplier.address = address;
		
		// creates supplier
		String supplierLocation = given()
				.body(supplier)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post(supplierURL)
				.then()
					.statusCode(Status.CREATED.getStatusCode())
					.extract().response().getHeader("Location");

		// retrieve created URI and extract supplier Id
		String[] elements = supplierLocation.split("/");
		supplierId = elements[elements.length - 1];
	}
	
	@Test 
	@Order(5) 
	void shouldFetchSupplier() {
		// fetch created supplier 
		supplier = given()
				.accept(ContentType.JSON)
				.pathParam("id", supplierId)
				.when()
				.get(supplierURL + "/{id}")
				.then()
					.statusCode(OK.getStatusCode())
					.contentType(ContentType.JSON)
					.extract().as(Supplier.class);
	}
	
	@Test
	@Order(6)
	void shouldCreatePurchase() {		
		Purchase purchase = new Purchase();
		purchase.purchaseDate = PURCHASE_DATE;
		purchase.purchaseQty = PURCHASE_QUANTITY;
		purchase.purchasePrice = PURCHASE_PRICE;
		purchase.invoiceNumber = INVOICE_NUMBER;
		purchase.supplier = supplier;
		
		String location = given()
				.body(purchase)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
					.statusCode(Status.CREATED.getStatusCode())
					.extract().response().getHeader("Location");
		
		assertThat(location).isNotNull().startsWith("http").contains("purchases");
		String[] segments = location.split("/");
		purchaseId = segments[segments.length - 1];
		assertThat(purchaseId).isNotNull();
	}
	
	@Test
	@Order(7)
	void shouldFindPurchases() {
		List<Purchase> purchases = given()
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.get()
				.then()
				 	.statusCode(OK.getStatusCode())
					.body("", is(not(empty())))
					.contentType(APPLICATION_JSON)
					.extract().body().as(getPurchasesTypeRef()); 
		
		assertThat(purchases)
				.hasSizeGreaterThanOrEqualTo(1)
				.filteredOn(purchase -> purchase.invoiceNumber.contains(INVOICE_NUMBER))
				.hasSize(1);
	}
		
	@Test
	@Order(8)
	void shouldFindPurchase() {
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", purchaseId)
			.when()
			.get("/{id}")
			.then()
			 	.statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
				.body("purchaseDate", is(PURCHASE_DATE.toString()))
				.body("purchaseQty", is(PURCHASE_QUANTITY))
				.body("purchasePrice", is(PURCHASE_PRICE.floatValue()))
				.body("invoiceNumber", is(INVOICE_NUMBER))
				.body("supplier.id", is(Integer.valueOf(supplierId)))
				.body("supplier.name", is(NAME))
				.body("supplier.email", is(EMAIL))
				.body("supplier.phone", is(PHONE))
				.body("supplier.supplierType", is(SupplierType.RETAILER.toString()))
				.body("supplier.description", is(DESCRIPTION))
				.body("supplier.address", is(notNullValue()));
	}
		
	@Test
	@Order(9)
	@DisabledOnIntegrationTest
	void shouldCountPurchasesPerSupplier() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.get(countEndpoint)
			.then()
				.statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON);
//				.extract().as(getPurchasesPerSupplierTypeRef());
//				.extract().response().body();
//		
//		assertThat(purchasPerSuppliers)
//				.hasSizeGreaterThanOrEqualTo(1)
//				.filteredOn(pps -> pps.supplierName.contains(NAME))
//				.hasSize(1);
	}
	
	@Test
	@Order(10)
	void shouldRetrievePurchasedComputers() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.queryParam("invoice", "invoiceNumber")
			.when()
			.get(computersEndpoint)
			.then()
				.statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
				.body("", is(emptyCollectionOf(Computer.class)))
				.body("", hasSize(0));
	}
	
	@Test
	@Order(11)
	void shouldFailToUpdateRandomPurchase() {
		final Long randomId = new Random().nextLong();
		 
		Purchase purchase = new Purchase();
		purchase.id = Long.valueOf(purchaseId);
		purchase.purchaseDate = UPDATED_PURCHASE_DATE;
		purchase.purchaseQty = UPDATED_PURCHASE_QUANTITY;
		purchase.purchasePrice = UPDATED_PURCHASE_PRICE;
		purchase.invoiceNumber = UPDATED_INVOICE_NUMBER;
		purchase.supplier = supplier;
		 
		given()
			.body(purchase)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", randomId)
			.when()
			.put("/{id}")
			.then()
				.statusCode(CONFLICT.getStatusCode());
	}
	
	@Test
	@Order(12)
	void shouldFailToUpdatePurchaseWithoutSupplier() {
	Purchase purchase = new Purchase();
	purchase.id = Long.valueOf(purchaseId);
	purchase.purchaseDate = UPDATED_PURCHASE_DATE;
	purchase.purchaseQty = UPDATED_PURCHASE_QUANTITY;
	purchase.purchasePrice = UPDATED_PURCHASE_PRICE;
	purchase.invoiceNumber = UPDATED_INVOICE_NUMBER;
	 
	given()
		.body(purchase)
		.header(CONTENT_TYPE, APPLICATION_JSON)
		.header(ACCEPT, APPLICATION_JSON)
		.pathParam("id", purchaseId)
		.when()
		.put("/{id}")
		.then()
			.statusCode(BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(13)
	void shouldFailToUpdateUnknownEmployee() {
		final Long randomId = new Random().nextLong();
		 
		Purchase purchase = new Purchase();
		purchase.id = randomId;
		purchase.purchaseDate = UPDATED_PURCHASE_DATE;
		purchase.purchaseQty = UPDATED_PURCHASE_QUANTITY;
		purchase.purchasePrice = UPDATED_PURCHASE_PRICE;
		purchase.invoiceNumber = UPDATED_INVOICE_NUMBER;
		purchase.supplier = supplier;
		 
		given()
			.body(purchase)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", randomId)
			.when()
			.put("/{id}")
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}
		
	@Test
	@Order(14)
	void shouldNotUpdateSupplierWhileUpdatingPurchase() {
		supplier.supplierType = SupplierType.WHOLESELLER; // Updating supplier
		Purchase purchase = new Purchase();
		purchase.id = Long.valueOf(purchaseId);
		purchase.purchaseDate = UPDATED_PURCHASE_DATE;
		purchase.purchaseQty = UPDATED_PURCHASE_QUANTITY;
		purchase.purchasePrice = UPDATED_PURCHASE_PRICE;
		purchase.invoiceNumber = UPDATED_INVOICE_NUMBER;
		purchase.supplier = supplier;
		 
		given()
			.body(purchase)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", purchaseId)
			.when()
			.put("/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}
	
	@Test
	@Order(15)
	void shouldCheckSupplierIsNotUpdated() {
		Purchase purchase = given()
				.header(ACCEPT, APPLICATION_JSON)
				.pathParam("id", purchaseId)
				.when()
				.get("/{id}")
				.then()
				 	.statusCode(OK.getStatusCode())
					.contentType(APPLICATION_JSON)
					.extract().as(Purchase.class);
		
		assertThat(purchase).extracting("invoiceNumber", "supplier.name", "supplier.email", "supplier.supplierType")
										 .doesNotContainNull()
										 .containsExactly(UPDATED_INVOICE_NUMBER, NAME, EMAIL, SupplierType.RETAILER);
	}
 
	@Test
	@Order(16)
	void shouldNotDeleteUnknownPurchase() { 
		Long randomId = new Random().nextLong(); 
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", randomId)
			.when()
			.delete("/{id}")
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}
	
	@Test
	@Order(17)
	void shouldDeletePurchase() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", purchaseId)
			.when()
			.delete("/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}

	@Test
	@Order(18)
	void shouldConfirmPurchaseIsDeleted() {		
		given()
			.accept(ContentType.JSON)
			.pathParam("id", purchaseId)
			.when()
			.get("/{id}")
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}
	
	@Test
	@Order(19)
	void shouldResetSuplier() {		
		// reset department table
		given()
			.accept(ContentType.JSON)
			.pathParam("id", supplierId)
			.when()
			.delete(supplierURL + "/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}

	@Test
	@Order(20)
	void shouldNotCreatePurchaseWhenSupplyingNullValuesForRequiredFields() {	
		Purchase purchase = new Purchase();
		purchase.purchaseDate = null;
		purchase.purchaseQty = PURCHASE_QUANTITY;
		purchase.purchasePrice = PURCHASE_PRICE;
		purchase.invoiceNumber = INVOICE_NUMBER;
		purchase.supplier = supplier;
		
		given()
			.body(purchase)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.post()
			.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(21)
	void shouldNotCreatePurchaseWithoutSupplier() {
		Purchase purchase = new Purchase();
		purchase.purchaseDate = PURCHASE_DATE;
		purchase.purchaseQty = PURCHASE_QUANTITY;
		purchase.purchasePrice = PURCHASE_PRICE;
		purchase.invoiceNumber = INVOICE_NUMBER;
//		purchase.supplier = supplier;
		
		given()
			.body(purchase)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.post()
			.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(22)
	void shouldNotCreatePurchaseWithInvalidSupplier() {
		supplier.id = null; // makes the supplier object invalid
		Purchase purchase = new Purchase();
		purchase.purchaseDate = PURCHASE_DATE;
		purchase.purchaseQty = PURCHASE_QUANTITY;
		purchase.purchasePrice = PURCHASE_PRICE;
		purchase.invoiceNumber = INVOICE_NUMBER;
		purchase.supplier = supplier;
		
		given()
			.body(purchase)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.post()
			.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(23)
	void shouldNotCreatePurchaseWithUnexistingSupplier() {
		supplier.id = new Random().nextLong(); // makes the supplier object not found from the DB
		Purchase purchase = new Purchase();
		purchase.purchaseDate = PURCHASE_DATE;
		purchase.purchaseQty = PURCHASE_QUANTITY;
		purchase.purchasePrice = PURCHASE_PRICE;
		purchase.invoiceNumber = INVOICE_NUMBER;
		purchase.supplier = supplier;
		
		given()
			.body(purchase)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.post()
			.then()
				.statusCode(Status.NOT_FOUND.getStatusCode());
	}

	private TypeRef<List<Purchase>> getPurchasesTypeRef() {
		return new TypeRef<List<Purchase>>() {
		};
	}

//	private TypeRef<List<PurchasePerSupplier>> getPurchasesPerSupplierTypeRef() {
//		return new TypeRef<List<PurchasePerSupplier>>() {
//		};
//	}

}

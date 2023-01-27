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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.assets.management.assets.model.entity.Address;
import com.assets.management.assets.model.entity.Computer;
import com.assets.management.assets.model.entity.Purchase;
import com.assets.management.assets.model.entity.Supplier;
import com.assets.management.assets.model.valueobject.Peripheral;
import com.assets.management.assets.model.valueobject.SupplierType;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestHTTPEndpoint(ComputerResource.class)
class ComputerResourceTest {
	// COMPUTERS
	private static final String BRAND = "Lenovo";
	private static final String MODEL_NAME = "ThinkBook 14 G2 ITL";
	private static final String MODEL_NUMBER = "MPNXB1927022";
	private static final String SERIAL_NUMBER = "MP23QNW3";
	private static final String PROCESSOR = "11th Gen Intel(R) Core(TM) i5-1135G7 @ 2.40GHz (8 CPUs), ~2.4GHz";
	private static final String MANUFACTURER = "Lenovo";
	private static final Integer MEMORY = 8192;
	private static final Integer STORAGE = 1048576;
	private static final Integer GRAPHIC_CARD = 1500;
	private static final Double DISPLAY_SIZE = Double.valueOf(14);
	private static final Set<Peripheral> PERIPHERALS = Stream.of(Peripheral.MOUSE, Peripheral.KEYBOARD)
																										.collect(Collectors.toCollection(HashSet::new));

	// UPDATED
	private static final String UPDATED_MODEL_NAME = "ThinkBook 14 G2 ITL - UPDATED";
	private static final String UPDATED_MODEL_NUMBER = "MPNXB192709Z";
	private static final String UPDATED_SERIAL_NUMBER = "C8QCFJ6FN72J - UPDATED";
	
	// PURCHASES 
	private static final LocalDate PURCHASE_DATE = LocalDate.of(2022, Month.JANUARY, 19);
	private static final Integer PURCHASE_QUANTITY = 4;
	private static final BigDecimal PURCHASE_PRICE = BigDecimal.valueOf(1850000).setScale(2, RoundingMode.HALF_UP);
	private static final String INVOICE_NUMBER = "TAONCTEST-123456PLC";
	private static final Integer UPDATED_QUANTITY = 10;
	
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
	private static Purchase purchase;
	private static String computerId;
	private static String supplierId;
	private static String purchaseId;
	
	@TestHTTPResource
	@TestHTTPEndpoint(SupplierResource.class)
	static URL supplierURL;
	
	@TestHTTPResource
	@TestHTTPEndpoint(PurchaseResource.class)
	static URL purchaseURL;
//	
//	@TestHTTPResource("computers")
//	@TestHTTPEndpoint(PurchaseResource.class)
//	URL computersEndpoint;
//	
	@Test
	@Order(1)
	void shouldListEmptyComputers() {
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
	void shouldNotFindRandomComputer() {
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
	void shouldNotCreateInvalidComputer() {
		final Computer computer = new Computer();
			given() 
				.body(computer)
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
				.post(purchaseURL)
				.then()
					.statusCode(Status.CREATED.getStatusCode()).log().ifError()
					.extract().response().getHeader("Location");
		
		assertThat(location).isNotNull().startsWith("http").contains("purchases");
		String[] segments = location.split("/");
		purchaseId = segments[segments.length - 1];
		assertThat(purchaseId).isNotNull();
	}
	
	
@Test
@Order(7)
void shouldFindPurchase() {
	purchase = given()
		.header(ACCEPT, APPLICATION_JSON)
		.pathParam("id", purchaseId)
		.when()
		.get(purchaseURL + "/{id}")
		.then()
		 	.statusCode(OK.getStatusCode())
			.contentType(APPLICATION_JSON)
			.extract().as(Purchase.class);
	
	assertThat(purchase).isNotNull();
}
	
	@Test
	@Order(8)
	void shouldCreateComputer() {		
		Computer computer = new Computer();
		computer.brand = BRAND;
		computer.model = MODEL_NAME;
		computer.modelNumber = MODEL_NUMBER;
		computer.serialNumber = SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
		computer.purchase = purchase;
		
		String location = given()
				.body(computer)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
					.statusCode(Status.CREATED.getStatusCode())
					.extract().response().getHeader("Location");
		
		assertThat(location).isNotNull().startsWith("http").contains("computers");
		String[] segments = location.split("/");
		computerId = segments[segments.length - 1];
		assertThat(computerId).isNotNull();
	}
	
	@Test
	@Order(9)
	void shouldLoadComputers() {
		List<Computer> computers = given()
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.get()
				.then()
				 	.statusCode(OK.getStatusCode())
					.body("", is(not(empty())))
					.contentType(APPLICATION_JSON)
					.extract().body().as(getComputersTypeRef()); 
		
		assertThat(computers)
				.hasSizeGreaterThanOrEqualTo(1)
				.filteredOn(computer -> computer.serialNumber.contains(SERIAL_NUMBER))
				.hasSize(1);
	}
		
	@Test
	@Order(10)
	void shouldFindComputer() {
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", computerId)
			.when()
			.get("/{id}")
			.then()
			 	.statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
				.body("brand", is(BRAND))
				.body("model", is(MODEL_NAME))
				.body("modelNumber", is(MODEL_NUMBER))
				.body("serialNumber", is(SERIAL_NUMBER))
				.body("processor", is(PROCESSOR))
				.body("manufacturer", is(MANUFACTURER))
				.body("storage", is(STORAGE))
				.body("memory", is(MEMORY))
				.body("graphicsCard", is(GRAPHIC_CARD))
				.body("displaySize", is(DISPLAY_SIZE.floatValue()))
//				.body("peripherals", hasItems(Peripheral.MOUSE, Peripheral.KEYBOARD))
				.body("purchase", is(notNullValue()));
	}
	
	@Test
	@Order(11)
	void shouldFailToUpdateRandomComputer() {
		final Long randomId = new Random().nextLong();
		 
		Computer computer = new Computer();
		computer.id = Long.valueOf(computerId);
		computer.brand = BRAND;
		computer.model = UPDATED_MODEL_NAME;
		computer.modelNumber = UPDATED_MODEL_NUMBER;
		computer.serialNumber = UPDATED_SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
		computer.purchase = purchase;
		 
		given()
			.body(computer)
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
	void shouldFailToUpdateComputerWithoutPurchase() {
		Computer computer = new Computer();
		computer.id = Long.valueOf(computerId);
		computer.brand = BRAND;
		computer.model = UPDATED_MODEL_NAME;
		computer.modelNumber = UPDATED_MODEL_NUMBER;
		computer.serialNumber = UPDATED_SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
	 
	given()
		.body(computer)
		.header(CONTENT_TYPE, APPLICATION_JSON)
		.header(ACCEPT, APPLICATION_JSON)
		.pathParam("id", computerId)
		.when()
		.put("/{id}")
		.then()
			.statusCode(BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(13)
	void shouldFailToUpdateUnknownComputer() {
		final Long randomId = new Random().nextLong();
		 
		Computer computer = new Computer();
		computer.id = randomId;
		computer.brand = BRAND;
		computer.model = UPDATED_MODEL_NAME;
		computer.modelNumber = UPDATED_MODEL_NUMBER;
		computer.serialNumber = UPDATED_SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
		computer.purchase = purchase;
		 
		given()
			.body(computer)
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
	void shouldNotUpdatePurchaseWhileUpdatingComputer() {
		purchase.purchaseQty = UPDATED_QUANTITY; // Updating PURCHASE
		 
		Computer computer = new Computer();
		computer.id = Long.valueOf(computerId);
		computer.brand = BRAND;
		computer.model = UPDATED_MODEL_NAME;
		computer.modelNumber = UPDATED_MODEL_NUMBER;
		computer.serialNumber = UPDATED_SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
		computer.purchase = purchase;
		 
		given()
			.body(computer)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", computerId)
			.when()
			.put("/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}
	
	@Test
	@Order(15)
	void shouldCheckSupplierIsNotUpdated() {
		Computer computer = given()
				.header(ACCEPT, APPLICATION_JSON)
				.pathParam("id", computerId)
				.when()
				.get("/{id}")
				.then()
				 	.statusCode(OK.getStatusCode())
					.contentType(APPLICATION_JSON)
					.extract().as(Computer.class);
		
		assertThat(computer).extracting("model", "modelNumber", "serialNumber", "purchase.purchaseQty")
										 .doesNotContainNull()
										 .containsExactly(UPDATED_MODEL_NAME, UPDATED_MODEL_NUMBER, UPDATED_SERIAL_NUMBER, PURCHASE_QUANTITY);
	}
 
	@Test
	@Order(16)
	void shouldNotDeleteUnknownComputer() { 
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
	void shouldDeleteComputer() {  
		given()
			.header(ACCEPT, APPLICATION_JSON)
			.pathParam("id", computerId)
			.when()
			.delete("/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}

	@Test
	@Order(18)
	void shouldConfirmComputerIsDeleted() {		
		given()
			.accept(ContentType.JSON)
			.pathParam("id", computerId)
			.when()
			.get("/{id}")
			.then()
				.statusCode(NOT_FOUND.getStatusCode());
	}

	@Test
	@Order(19)
	void shouldResetPurchase() {		
		// reset purchase table
		given()
			.accept(ContentType.JSON)
			.pathParam("id", purchaseId)
			.when()
			.delete(purchaseURL + "/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}
	
	@Test
	@Order(20)
	void shouldResetSuplier() {		
		// reset supplier table
		given()
			.accept(ContentType.JSON)
			.pathParam("id", supplierId)
			.when()
			.delete(supplierURL + "/{id}")
			.then()
				.statusCode(NO_CONTENT.getStatusCode());
	}

	@Test
	@Order(21)
	void shouldNotCreateComputerWhenSupplyingNullValuesForRequiredFields() {	
		Computer computer = new Computer();
		computer.brand = BRAND;
		computer.model = MODEL_NAME;
		computer.modelNumber = MODEL_NUMBER;
		computer.serialNumber = null;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
		computer.purchase = purchase;
		
		given()
				.body(computer)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
					.statusCode(Status.BAD_REQUEST.getStatusCode());
	}

	@Test
	@Order(22)
	void shouldNotCreateComputerWithoutPurchase() {
		Computer computer = new Computer();
		computer.brand = BRAND;
		computer.model = MODEL_NAME;
		computer.modelNumber = MODEL_NUMBER;
		computer.serialNumber = SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
//		computer.purchase = purchase;
		
		given()
				.body(computer)
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(ACCEPT, APPLICATION_JSON)
				.when()
				.post()
				.then()
					.statusCode(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(23)
	void shouldNotCreatePurchaseWithInvalidSupplier() {
		purchase.id = null; // makes the purchase object invalid
		
		Computer computer = new Computer();
		computer.brand = BRAND;
		computer.model = MODEL_NAME;
		computer.modelNumber = MODEL_NUMBER;
		computer.serialNumber = SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
		computer.purchase = purchase;
		
		given()
			.body(computer)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.post()
			.then()
				.statusCode(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@Order(24)
	void shouldNotCreatePurchaseWithUnexistingSupplier() {
		purchase.id = new Random().nextLong(); // makes the purchase object not found from the DB
		
		Computer computer = new Computer();
		computer.brand = BRAND;
		computer.model = MODEL_NAME;
		computer.modelNumber = MODEL_NUMBER;
		computer.serialNumber = SERIAL_NUMBER;
		computer.manufacturer = MANUFACTURER;
		computer.processor = PROCESSOR;
		computer.memory = MEMORY;
		computer.storage = STORAGE;
		computer.graphicsCard = GRAPHIC_CARD;
		computer.displaySize = DISPLAY_SIZE;
		computer.peripherals = PERIPHERALS;
		computer.purchase = purchase;
		
		given()
			.body(computer)
			.header(CONTENT_TYPE, APPLICATION_JSON)
			.header(ACCEPT, APPLICATION_JSON)
			.when()
			.post()
			.then()
				.statusCode(Status.NOT_FOUND.getStatusCode());
	}

	private TypeRef<List<Computer>> getComputersTypeRef() {
		return new TypeRef<List<Computer>>() {
		};
	}

}

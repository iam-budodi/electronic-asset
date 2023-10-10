package com.japhet_sebastian.procurement.purchase;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import com.japhet_sebastian.exception.ErrorResponse;
import com.japhet_sebastian.organization.entity.AddressDto;
import com.japhet_sebastian.procurement.supplier.SupplierDto;
import com.japhet_sebastian.procurement.supplier.SupplierResource;
import com.japhet_sebastian.procurement.supplier.SupplierType;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


@QuarkusTest
@TestHTTPEndpoint(PurchaseResource.class)
@QuarkusTestResource(KeycloakResource.class)
class PurchaseResourceTest extends AccessTokenProvider {

    @TestHTTPResource
    @TestHTTPEndpoint(SupplierResource.class)
    String supplierResource;

    @TestHTTPResource("select")
    @TestHTTPEndpoint(PurchaseResource.class)
    String select;

    @Test
    void shouldGetPurchases() {
        String totalItem = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .header(ACCEPT, APPLICATION_JSON)
                .when().get()
                .then()
                .statusCode(OK.getStatusCode())
                .body("$", is(not(empty())))
                .body("size()", is(greaterThanOrEqualTo(2)))
                .body(
                        containsString("RPC4-123456"),
                        containsString("RPC4-987654")
                ).extract().response().getHeader("X-Total-Count");

        assertThat(Integer.valueOf(totalItem), is(greaterThanOrEqualTo(2)));
    }

    @Test
    void shouldGetPagedPurchaseList() {
        Response response = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .contentType(ContentType.JSON)
                .when().get("?page=0&size=1")
                .then()
                .statusCode(OK.getStatusCode())
                .extract().response();

        List<PurchaseDto> purchases = response.jsonPath().getList("$");
        assertThat(purchases, is(not(empty())));
        assertThat(purchases, hasSize(greaterThanOrEqualTo(1)));
        assertThat(Integer.valueOf(response.getHeader("X-Total-Count")), is(greaterThanOrEqualTo(1)));
    }

    @Test
    void getFormSelectionOptions() {
        given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .contentType(ContentType.JSON)
                .when().get(select)
                .then()
                .statusCode(OK.getStatusCode())
                .body("$.size()", is(greaterThanOrEqualTo(1)))
                .body(
                        containsString("RPC4-987654"),
                        containsString("RPC4-123456")
                );
    }

    @Test
    void ShouldSavePurchase() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("randomsupport@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));
    }

    @Test
    void getPurchaseById() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));

        // get purchase by id
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(purchaseUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("Technology Associate"),
                        containsString("support@techassociate.co.tz"),
                        containsString("0744608510")
                )
                .extract().as(PurchaseDto.class);
    }

    @Test
    void shouldGetPurchaseRecordNotFound() {
        final String stringUUID = UUID.randomUUID().toString();
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(stringUUID)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void shouldFailToSavePurchaseInvoiceNumberIsMissing() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("missing.invoice@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(null);
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                        new ErrorResponse.ErrorMessage("makePurchase.purchase.invoiceNumber", getErrorMessage("Purchase.invoice-number.required"))
                )
        );
    }

    @Test
    void shouldFailToSavePurchaseInvoiceNumberIsDuplicated() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("duplicated.invoice@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                        new ErrorResponse.ErrorMessage("Purchase record already exists!")
                )
        );
    }

    @Test
    void shouldFailToSavePurchaseMissingRequiredFields() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("missing.fields@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = new PurchaseDto();
        purchase.setSupplier(supplier);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(4)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                        new ErrorResponse.ErrorMessage("makePurchase.purchase.invoiceNumber", getErrorMessage("Purchase.invoice-number.required")),
                        new ErrorResponse.ErrorMessage("makePurchase.purchase.purchaseDate", getErrorMessage("Purchase.date.required")),
                        new ErrorResponse.ErrorMessage("makePurchase.purchase.purchasePrice", getErrorMessage("Purchase.price.required")),
                        new ErrorResponse.ErrorMessage("makePurchase.purchase.purchaseQty", getErrorMessage("Purchase.quantity.required"))
                )
        );
    }

    @Test
    void shouldFailToSavePurchaseWithoutSupplier() {
        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        purchase.setSupplier(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(), containsInAnyOrder(
                        new ErrorResponse.ErrorMessage("Invalid supplier")
                )
        );
    }

    @Test
    void shouldUpdatePurchase() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("update@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));

        // get purchase by id
        PurchaseDto foundPurchase = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(purchaseUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(PurchaseDto.class);

        // update purchase
        purchase.setPurchaseId(foundPurchase.getPurchaseId());
        purchase.setInvoiceNumber("UPT123456INV");
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).put(purchaseUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // Confirm purchase was updated
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(purchaseUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .body(
                        containsString("UPT123456INV")
                );
    }

    @Test
    void shouldFailToUpdatePurchaseWIthNullId() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("failupdate@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));

        // update purchase
        purchase.setInvoiceNumber("UPT123456INV");
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).put(purchaseUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Purchase does not have purchaseId"))
        );
    }

    @Test
    void shouldFailToUpdatePurchaseWIthEmptyId() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("failemptyidupdate@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));

        // update purchase
        purchase.setPurchaseId("");
        purchase.setInvoiceNumber("UPT123456INV");
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).put(purchaseUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Purchase does not have purchaseId"))
        );
    }

    @Test
    void shouldFailToUpdatePurchaseWIthIdMismatch() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("failupdateidmismatch@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));

        // update purchase
        purchase.setPurchaseId(UUID.randomUUID().toString());
        purchase.setInvoiceNumber("UPT123456INV");
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).put(purchaseUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Path variable purchaseId does not match Purchase.purchaseId"))
        );
    }

    @Test
    void shouldFailToUpdatePurchaseWithNullSupplier() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("failupdatenosupplier@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));

        // get purchase by id
        PurchaseDto foundPurchase = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(purchaseUUID)
                .then()
                .statusCode(OK.getStatusCode())
                .extract().as(PurchaseDto.class);

        // update purchase
        purchase.setPurchaseId(foundPurchase.getPurchaseId());
        purchase.setInvoiceNumber("2UPT123456INV");
        purchase.setSupplier(null);
        ErrorResponse errorResponse = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).put(purchaseUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage("Invalid input"))
        );
    }

    @Test
    void shouldDeletePurchase() {
        final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        // create supplier
        final SupplierDto supplier = createSupplier();
        supplier.setCompanyName(RandomStringUtils.randomAlphabetic(24));
        supplier.setCompanyEmail("deletepurchase@techassociate.co.tz");
        supplier.setCompanyPhone(RandomStringUtils.randomNumeric(10));
        String supplierURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
                .body(supplier).post(supplierResource)
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(supplierURL, notNullValue());
        String supplierUUID = supplierURL.substring(supplierURL.lastIndexOf("/") + 1);
        assertThat(supplierUUID, matchesRegex(UUID_REGEX));

        // create purchase
        final PurchaseDto purchase = createPurchase();
        purchase.setInvoiceNumber(RandomStringUtils.randomAlphanumeric(10));
        supplier.setSupplierId(supplierUUID);
        purchase.setSupplier(supplier);
        String purchaseURL = given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .body(purchase).post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().response().getHeader("Location");

        assertThat(purchaseURL, notNullValue());
        String purchaseUUID = purchaseURL.substring(purchaseURL.lastIndexOf("/") + 1);
        assertThat(purchaseUUID, matchesRegex(UUID_REGEX));

        // update purchase
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().delete(purchaseUUID)
                .then()
                .statusCode(NO_CONTENT.getStatusCode());

        // verify purchase delete
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().get(purchaseUUID)
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void deletePurchaseFailsNotFound() {
        String supplierUUID = UUID.randomUUID().toString();
        ErrorResponse errorResponse = given()
                .auth().oauth2(getAccessToken("habiba.baanda", "baanda"))
                .when().delete(supplierUUID)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ErrorResponse.class);

        assertThat(errorResponse.getErrorId(), is(nullValue()));
        assertThat(errorResponse.getErrors(), allOf(notNullValue(), hasSize(1)));
        assertThat(errorResponse.getErrors(),
                contains(new ErrorResponse.ErrorMessage(String.format("No purchase found for purchaseId[%s]", supplierUUID)))
        );
    }

    private PurchaseDto createPurchase() {
        final PurchaseDto purchase = new PurchaseDto();
        purchase.setPurchaseDate(LocalDate.of(2022, Month.JULY, 31));
        purchase.setPurchasePrice(new BigDecimal(18000000));
        purchase.setPurchaseQty(200);
        purchase.setInvoiceNumber("xxxyyzzz1234");
        return purchase;
    }

    private SupplierDto createSupplier() {
        final AddressDto address = address();
        final SupplierDto supplier = new SupplierDto();
        supplier.setCompanyName("Technology Associate");
        supplier.setCompanyEmail("support@techassociate.co.tz");
        supplier.setCompanyPhone("0744608510");
        supplier.setSupplierType(SupplierType.WHOLESALER);
        supplier.setAddress(address);
        return supplier;
    }

    private AddressDto address() {
        final AddressDto address = new AddressDto();
        address.setStreet("ohio street");
        address.setPostalCode("19119");
        address.setDistrict("Ilala");
        address.setCity("Dar es salaam");
        address.setCountry("Tanzania");
        return address;
    }

    private String getErrorMessage(String key) {
        return ResourceBundle.getBundle("ValidationMessages").getString(key);
    }

}
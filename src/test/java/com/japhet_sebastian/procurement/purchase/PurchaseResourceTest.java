package com.japhet_sebastian.procurement.purchase;

import com.japhet_sebastian.AccessTokenProvider;
import com.japhet_sebastian.KeycloakResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.HttpHeaders.ACCEPT;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


@QuarkusTest
@TestHTTPEndpoint(PurchaseResource.class)
@QuarkusTestResource(KeycloakResource.class)
class PurchaseResourceTest extends AccessTokenProvider {
    private static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    @Test
    void shouldGetPurchases() {
        String totalItem = given()
                .auth().oauth2(getAccessToken("lulu.shaban", "shaban"))
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
}
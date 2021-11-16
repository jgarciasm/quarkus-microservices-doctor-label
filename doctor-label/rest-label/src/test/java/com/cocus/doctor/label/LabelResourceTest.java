package com.cocus.doctor.label;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LabelResourceTest {

    private static final String DEFAULT_CODE = "3X4MPL3";
    private static final String UPDATED_CODE = "3X4MPL3 (updated)";
    private static final String DEFAULT_DESCRIPTION = "Anxiety disorder";
    private static final String UPDATED_DESCRIPTION = "Anxiety disorder (updated)";
    private static final String UPDATED_OTHER_CODE = "3X4MPL3 (updated again)";

    private static final int NB_LABELS = 122;
    private static String labelId;

    @Test
    public void testHelloEndpoint() {
        given()
            .when().get("/api/labels/hello")
            .then()
            .statusCode(200)
            .body(is("hello"));
    }

    @Test
    void shouldNotGetUnknownLabel() {
        Long randomId = new Random().nextLong();
        given()
            .pathParam("id", randomId)
            .when().get("/api/labels/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldNotAddInvalidItem() {
        Label label = new Label();
        label.code = null;
        label.description = DEFAULT_DESCRIPTION;

        given()
            .body(label)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/labels")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<Label> labels = get("/api/labels").then()
            .statusCode(OK.getStatusCode())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .extract().body().as(getLabelTypeRef());
        assertEquals(NB_LABELS, labels.size());
    }

    @Test
    @Order(2)
    void shouldAddAnItem() {
        Label label = new Label();
        label.code = DEFAULT_CODE;
        label.description = DEFAULT_DESCRIPTION;

        String location = given()
            .body(label)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/labels")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().header("Location");
        assertTrue(location.contains("/api/labels"));

        // Stores the id
        String[] segments = location.split("/");
        labelId = segments[segments.length - 1];
        assertNotNull(labelId);

        given()
            .pathParam("id", labelId)
            .when().get("/api/labels/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("code", Is.is(DEFAULT_CODE))
            .body("description", Is.is(DEFAULT_DESCRIPTION));

        List<Label> labels = get("/api/labels").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getLabelTypeRef());
        assertEquals(NB_LABELS + 1, labels.size());
    }

    @Test
    @Order(3)
    void shouldUpdateAnItem() {
        Label label = new Label();
        label.id = Long.valueOf(labelId);
        label.code = UPDATED_CODE;
        label.description = UPDATED_DESCRIPTION;

        given()
            .body(label)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .put("/api/labels")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("code", Is.is(UPDATED_CODE))
            .body("description", Is.is(UPDATED_DESCRIPTION));

        List<Label> labels = get("/api/labels").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getLabelTypeRef());
        assertEquals(NB_LABELS + 1, labels.size());
    }

    @Test
    @Order(4)
    void shouldRemoveAnItem() {
        given()
            .pathParam("id", labelId)
            .when().delete("/api/labels/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        List<Label> labels = get("/api/labels").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getLabelTypeRef());
        assertEquals(NB_LABELS, labels.size());
    }

    @Test
    void shouldPingOpenAPI() {
        given()
            .header(ACCEPT, APPLICATION_JSON)
            .when().get("/openapi")
            .then()
            .statusCode(OK.getStatusCode());
    }

    @Test
    void shouldPingSwaggerUI() {
        given()
            .when().get("/swagger-ui")
            .then()
            .statusCode(OK.getStatusCode());
    }

    private TypeRef<List<Label>> getLabelTypeRef() {
        return new TypeRef<List<Label>>() {
            // Kept empty on purpose
        };
    }
}

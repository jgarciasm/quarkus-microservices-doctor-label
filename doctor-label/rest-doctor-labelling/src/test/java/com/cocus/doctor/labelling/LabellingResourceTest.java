package com.cocus.doctor.labelling;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import java.util.Random;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.HttpHeaders.ACCEPT;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cocus.doctor.labelling.client.Label;
import com.cocus.doctor.labelling.client.MockLabelService;

@QuarkusTest
@QuarkusTestResource(DatabaseResource.class)
//@QuarkusTestResource(KafkaResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LabellingResourceTest {

    private static final String DEFAULT_CASE_DESCRIPTION = "Default case description";
    private static final String DEFAULT_DOCTOR_ID = "D0ct0R";
    private static final int DEFAULT_LABEL = 1;
    private static final int DEFAULT_CASE_LABEL_TIME = 600;
    private static final String UPDATED_CASE_DESCRIPTION = "Updated case description";
    private static final String UPDATED_DOCTOR_ID = "D0ct0R-UPD4T3D";
    private static final int UPDATED_LABEL = 2;
    private static final int UPDATED_CASE_LABEL_TIME = 300;
    private static final String DEFAULT_LABEL_CODE = "L4B3L-C0D3";
    private static final String DEFAULT_LABEL_DESCRIPTION = "D3SCR1PT10N";

    private static final int NB_CASES = 3;
    private static String caseId;

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

    @Test
    public void testHelloEndpoint() {
        given()
            .when().get("/api/cases/hello")
            .then()
            .statusCode(200)
            .body(is("hello"));
    }

    @Test
    void shouldNotGetUnknownCase() {
        Long randomId = new Random().nextLong();
        given()
            .pathParam("id", randomId)
            .when().get("/api/cases/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void shouldNotAddInvalidItem() {
        MedicalCase cas = new MedicalCase();
        cas.description = null;
        cas.doctorId = null;

        given()
            .body(cas)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/cases")
            .then()
            .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    @Order(1)
    void shouldGetInitialItems() {
        List<MedicalCase> cases = get("/api/cases").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getCaseTypeRef());
        assertEquals(NB_CASES, cases.size());
    }
    
    @Test
    @Order(2)
    void shouldGetInitialItemsFilterByLabel() {
        List<MedicalCase> cases = get("/api/cases?filterByLabel=2").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getCaseTypeRef());
        assertEquals(NB_CASES -1, cases.size());
    }

    @Test
    @Order(3)
    void shouldAddACase() {
        MedicalCase cas = new MedicalCase();
        cas.description = DEFAULT_CASE_DESCRIPTION;
        cas.doctorId = DEFAULT_DOCTOR_ID;
        cas.label = Long.valueOf(DEFAULT_LABEL);
        cas.caseLabelTime = DEFAULT_CASE_LABEL_TIME;

        String location = given()
            .body(cas)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .post("/api/cases")
            .then()
            .statusCode(CREATED.getStatusCode())
            .extract().header("Location");
        
        assertTrue(location.contains("/api/cases"));
        
        // Stores the id
        String[] segments = location.split("/");
        caseId = segments[segments.length - 1];
        assertNotNull(caseId);

        given()
            .pathParam("id", caseId)
            .when().get("/api/cases/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("description", Is.is(DEFAULT_CASE_DESCRIPTION))
            .body("doctorId", Is.is(DEFAULT_DOCTOR_ID))
            .body("label.id", Is.is(DEFAULT_LABEL))
            .body("caseLabelTime", Is.is(DEFAULT_CASE_LABEL_TIME));

        List<MedicalCase> cases = get("/api/cases").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getCaseTypeRef());
        assertEquals(NB_CASES + 1, cases.size());
    }
    
    @Test
    @Order(4)
    void shouldUpdateACase() {
    	MedicalCase cas = new MedicalCase();
    	cas.id = Long.valueOf(caseId);
        cas.description = UPDATED_CASE_DESCRIPTION;
        cas.doctorId = UPDATED_DOCTOR_ID;
        cas.label = Long.valueOf(UPDATED_LABEL);
        cas.caseLabelTime = UPDATED_CASE_LABEL_TIME;

        given()
            .body(cas)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .when()
            .put("/api/cases")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("description", Is.is(UPDATED_CASE_DESCRIPTION))
            .body("doctorId", Is.is(UPDATED_DOCTOR_ID))
            .body("label", Is.is(UPDATED_LABEL))
            .body("caseLabelTime", Is.is(UPDATED_CASE_LABEL_TIME));

        List<MedicalCase> cases = get("/api/cases").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getCaseTypeRef());
        assertEquals(NB_CASES + 1, cases.size());
    }
    
    @Test
    @Order(5)
    void shouldAddANewLabelToACase() {
        Label label = new Label();
        label.code = DEFAULT_LABEL_CODE;
        label.description = DEFAULT_LABEL_DESCRIPTION;

        given()
            .body(label)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(ACCEPT, APPLICATION_JSON)
            .pathParam("id", caseId)
            .when()
            .post("/api/cases/{id}/newLabel")
            .then()
            .statusCode(OK.getStatusCode());

        given()
            .pathParam("id", caseId)
            .when().get("/api/cases/{id}")
            .then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .body("label.id", Is.is(MockLabelService.DEFAULT_LABEL_ID));

        List<MedicalCase> cases = get("/api/cases").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getCaseTypeRef());
        assertEquals(NB_CASES + 1, cases.size());
    }
    
    @Test
    @Order(6)
    void shouldDeleteALabelFromACase() {

    	given()
        .pathParam("id", caseId)
        .when().delete("/api/cases/{id}/deleteLabel")
        .then()
        .statusCode(OK.getStatusCode());

    }
    
    @Test
    @Order(7)
    void shouldRemoveACase() {
        given()
            .pathParam("id", caseId)
            .when().delete("/api/cases/{id}")
            .then()
            .statusCode(NO_CONTENT.getStatusCode());

        List<MedicalCase> cases = get("/api/cases").then()
            .statusCode(OK.getStatusCode())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .extract().body().as(getCaseTypeRef());
        assertEquals(NB_CASES, cases.size());
    }

    private TypeRef<List<MedicalCase>> getCaseTypeRef() {
        return new TypeRef<List<MedicalCase>>() {
            // Kept empty on purpose
        };
    }
    
}
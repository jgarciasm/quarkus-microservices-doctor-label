package com.cocus.doctor.labelling;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import com.cocus.doctor.labelling.client.Label;

import io.quarkus.vertx.http.runtime.devmode.Json.JsonObjectBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/api/cases")
@Produces(APPLICATION_JSON)
public class LabellingResource {

    private static final Logger LOGGER = Logger.getLogger(LabellingResource.class);

    @Inject
    LabellingService service;
    
    private AtomicLong counter = new AtomicLong(0);

    @Tag(name = "cases", description = "Anybody interested in cases")
    @Operation(summary = "Returns all the cases from the database or cases filtered by label id")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = MedicalCase.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No cases")
    @GET
    public Response getAllCases(@Parameter(description = "Label identifier", required = false)
    @QueryParam("filterByLabel") Long filterByLabel) {
    	if(Objects.nonNull(filterByLabel)) {
    		List<MedicalCase> cases = service.findCasesByLabel(filterByLabel);
            if (cases != null) {
                LOGGER.debug("Found cases " + cases);
                return Response.ok(cases).build();
            } else {
                LOGGER.debug("No cases found with labelId " + filterByLabel);
                return Response.noContent().build();
            }
    	} else {
    		List<MedicalCase> cases = service.findAllCases();
    		if (cases != null) {
    			LOGGER.debug("Total number of cases " + cases);
                return Response.ok(cases).build();
    		} else {
    			LOGGER.debug("No cases found" + filterByLabel);
                return Response.noContent().build();
    		}
    	    
    	}
    	
    }

    @Tag(name = "cases", description = "Anybody interested in cases")
    @Operation(summary = "Returns a case for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = MedicalCase.class)))
    @APIResponse(responseCode = "204", description = "The case is not found for a given identifier")
    @GET
    @Path("/{id}")
    public Response getCase(@Parameter(description = "Case identifier", required = true) @PathParam("id") Long id) {
        MedicalCase cas = service.findCaseById(id);
        if (cas != null) {
            LOGGER.debug("Found case " + cas);
            Label label = service.findLabelOfCase(cas.label);
            String completeCase = cas.completeJsonCase(label);
            return Response.ok(completeCase).build();
        } else {
            LOGGER.debug("No case found with id " + id);
            return Response.noContent().build();
        }
    }

    @Tag(name = "cases", description = "Anybody interested in cases")
    @Operation(summary = "Creates a new case")
    @APIResponse(responseCode = "201", description = "The URI of the created case", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @POST
    public Response createCase(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = MedicalCase.class)))
        @Valid MedicalCase cas, @Context UriInfo uriInfo) {
        cas = service.persistCase(cas);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(cas.id));
        LOGGER.debug("New case created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }
    
    @Tag(name = "cases", description = "Anybody interested in cases")
    @Operation(summary = "Updates an exiting case")
    @APIResponse(responseCode = "200", description = "The updated case", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = MedicalCase.class)))
    @PUT
    public Response updateCase(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = MedicalCase.class)))
        @Valid MedicalCase cas) {
        cas = service.updateCase(cas);
        LOGGER.debug("Case updated with new value " + cas);
        return Response.ok(cas).build();
    }
    
    @Tag(name = "cases", description = "Anybody interested in cases")
    @Operation(summary = "Deletes an exiting case")
    @APIResponse(responseCode = "204")
    @DELETE
    @Path("/{id}")
    public Response deleteCase(
        @Parameter(description = "Label identifier", required = true)
        @PathParam("id") Long id) {
        service.deleteCase(id);
        LOGGER.debug("Case deleted with " + id);
        return Response.noContent().build();
    }
    
    @Tag(name = "cases", description = "Anybody interested in cases")
    @Operation(summary = "Associates a new label to an existing case. The new label is created from label service by this call first.")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class, required = true)))
    @APIResponse(responseCode = "202", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class, required = true)))
    @POST
    @Path("/{id}/newLabel")
    @Retry(maxRetries = 4)
    public Response newLabelToCase(
            @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class)))
            Label label,
            @PathParam("id") Long id) {
    	
    	final Long invocationNumber = counter.getAndIncrement();
    	
    	MedicalCase cas = service.newLabelToCase(label, id);
    	Label createdLabel = service.findLabelOfCase(cas.label);
        String completeCase = cas.completeJsonCase(createdLabel);
        
        if(label.code.equals(createdLabel.code)) {
        	LOGGER.debug("Invocation #" + invocationNumber + " - Successfully Label " + label.code + " creation and Case updated with new label");
        	return Response.ok(completeCase).build();
        }
        else {
        	LOGGER.debug("Invocation #" + invocationNumber + " - Failed Label " + label.code + " creation. Service is unavailable temporary");
        	return Response.notModified(completeCase).build();
        }
        	
    }
    
    @Tag(name = "cases", description = "Anybody interested in cases")
    @Operation(summary = "Deletes the label of an existing case. The label is removed from label service by this call first.")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class, required = true)))
    @DELETE
    @Path("/{id}/deleteLabel")
    @Retry(maxRetries = 4)
    public Response deleteLabelFromCase(
            @PathParam("id") Long id) {
    	
    	final Long invocationNumber = counter.getAndIncrement();
    	
    	MedicalCase cas = service.findCaseById(id);
        if (cas != null) {
            LOGGER.debug("Found case " + cas);
            boolean deleted = service.deleteLabel(cas.label);
            if(deleted) {
            	cas.label = null;
                LOGGER.debug("Invocation #" + invocationNumber + " - Successfully Label deletion");
                return Response.ok(cas).build();
            } else {
            	Label oldLabel = service.findLabelOfCase(cas.label);
                String completeCase = cas.completeJsonCase(oldLabel);
            	LOGGER.debug("Invocation #" + invocationNumber + " - Failed Label deletion. Service is unavailable temporary");
                return Response.notModified(completeCase).build();
            }
        } else {
            LOGGER.debug("No case found with id " + id);
            return Response.noContent().build();
        }
    }

    @Tag(name = "admin", description = "Administration endpoints")
    @Operation(summary = "Verifies if the service is running")
    @GET
    @Produces(TEXT_PLAIN)
    @Path("/hello")
    public String hello() {
        return "hello";
    }
}

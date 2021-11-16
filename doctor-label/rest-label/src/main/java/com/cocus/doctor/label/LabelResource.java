package com.cocus.doctor.label;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/api/labels")
@Produces(APPLICATION_JSON)
public class LabelResource {

    private static final Logger LOGGER = Logger.getLogger(LabelResource.class);

    @Inject
    LabelService service;

    @Tag(name = "labels", description = "Anybody interested in labels")
    @Operation(summary = "Returns all the labels from the database")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class, type = SchemaType.ARRAY)))
    @APIResponse(responseCode = "204", description = "No labels")
    @GET
    public Response getAllLabels() {
        List<Label> labels = service.findAllLabels();
        LOGGER.debug("Total number of labels " + labels);
        return Response.ok(labels).build();
    }

    @Tag(name = "labels", description = "Anybody interested in labels")
    @Operation(summary = "Returns a label for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class)))
    @APIResponse(responseCode = "204", description = "The label is not found for a given identifier")
    @GET
    @Path("/{id}")
    public Response getLabel(
        @Parameter(description = "Label identifier", required = true)
        @PathParam("id") Long id) {
        Label label = service.findLabelById(id);
        if (label != null) {
            LOGGER.debug("Found label " + label);
            return Response.ok(label).build();
        } else {
            LOGGER.debug("No label found with id " + id);
            return Response.noContent().build();
        }
    }

    @Tag(name = "labels", description = "Anybody interested in labels")
    @Operation(summary = "Creates a valid label")
    @APIResponse(responseCode = "201", description = "The URI of the created label", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    @POST
    public Response createLabel(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class)))
        @Valid Label label, @Context UriInfo uriInfo) {
        label = service.persistLabel(label);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(label.id));
        LOGGER.debug("New label created with URI " + builder.build().toString());
        //return Response.ok(label).header("Location", builder.build()).build();
        return Response.created(builder.build()).build();
    }

    @Tag(name = "labels", description = "Anybody interested in labels")
    @Operation(summary = "Updates an exiting label")
    @APIResponse(responseCode = "200", description = "The updated label", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class)))
    @PUT
    public Response updateLabel(
        @RequestBody(required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Label.class)))
        @Valid Label label) {
        label = service.updateLabel(label);
        LOGGER.debug("Label updated with new value " + label);
        return Response.ok(label).build();
    }

    @Tag(name = "labels", description = "Anybody interested in labels")
    @Operation(summary = "Deletes an exiting label")
    @APIResponse(responseCode = "204")
    @DELETE
    @Path("/{id}")
    public Response deleteLabel(
        @Parameter(description = "Label identifier", required = true)
        @PathParam("id") Long id) {
        service.deleteLabel(id);
        LOGGER.debug("Label deleted with " + id);
        return Response.noContent().build();
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

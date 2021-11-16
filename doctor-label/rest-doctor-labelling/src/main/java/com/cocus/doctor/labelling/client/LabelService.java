package com.cocus.doctor.labelling.client;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/labels")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient
public interface LabelService {

    @POST
    Response addLabel(Label label);
    
    @GET
    List<Label> getLabels();
    
    @GET
    @Path("/{id}")
    Label getLabel(@PathParam long id);
    
    @DELETE
    @Path("/{id}")
    void deleteLabel(@PathParam long id);

}

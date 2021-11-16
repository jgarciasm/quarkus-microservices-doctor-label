package com.cocus.doctor.labelling;

import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
@OpenAPIDefinition(
    info = @Info(title = "Labelling API",
        description = "This API manage and persist the Doctor cases and labels",
        version = "1.0",
        contact = @Contact(name = "Jonad García", url = "https://github.com/jgarciasm")),
    servers = {
        @Server(url = "http://localhost:8082")
    },
    tags = {
        @Tag(name = "cases", description = "Anybody interested in cases")
    }
)
public class LabellingApplication extends Application {
}

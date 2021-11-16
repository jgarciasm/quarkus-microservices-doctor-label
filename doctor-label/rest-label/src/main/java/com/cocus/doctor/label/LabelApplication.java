package com.cocus.doctor.label;

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
    info = @Info(title = "Label API",
        description = "This API allows CRUD operations on Label database",
        version = "1.0",
        contact = @Contact(name = "Jonad García", url = "https://github.com/jgarciasm")),
    servers = {
        @Server(url = "http://localhost:8083")
    },
    tags = {
        @Tag(name = "labels", description = "Anybody interested in labels")
    }
)
public class LabelApplication extends Application {
}

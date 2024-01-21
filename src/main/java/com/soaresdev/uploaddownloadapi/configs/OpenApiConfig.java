package com.soaresdev.uploaddownloadapi.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Linkedin",
                        url = "https://www.linkedin.com/in/hiago-soares-96840a271/"
                ),
                description = "Documentation for upload and download files API.\n\nOperations can be performed in the system or in a database",
                title = "Upload and download files API",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Localhost server",
                        url = "http://localhost:8080/"
                )
        }
)
public class OpenApiConfig {
}
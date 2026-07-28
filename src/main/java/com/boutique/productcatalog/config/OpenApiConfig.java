package com.boutique.productcatalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productCatalogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Boutique Product Catalog Service API")
                        .description(
                                "REST API for creating and retrieving products " +
                                "in the Boutique microservices platform."
                        )
                        .version("v1")
                        .contact(new Contact()
                                .name("Boutique Platform Engineering Team"))
                        .license(new License()
                                .name("Internal Project")));
    }
}
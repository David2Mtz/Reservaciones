package com.ipn.mx.reservaciones7cm3.core.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {
    @Bean
    public OpenAPI myOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("API para la gestion de reservaciones")
                        .version("1.0.0")
                        .description("API REST para la gestion de cuartos y reservaciones")
                        .contact(new Contact()
                                .name("Luis David Martinez")
                                .email("ld.martinez.117@gmail.com")
                                .url("https://asdfawef.com")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("basicScheme"))
                .components(new Components()
                        .addSecuritySchemes("basicScheme", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Introduzca su usuario y contrasena para acceder a las operaciones protegidas.")
                        )
                );
    }
}

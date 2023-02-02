package se.sigma.boostapp.boost_app_java.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for BoostApp Step-Api.
 * This class creates a {@link OpenAPI} object
 * and sets the security requirements, components, servers and info for the BoostApp Step-Api.
 * The security requirement added is the "Bearer Authorization" using JWT.
 * The API info includes the title, description, and version.
 *
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * Creates a custom {@link OpenAPI} object for BoostApp Step-Api.
     *
     * @return {@link OpenAPI} object with security requirements, components,
     *  servers and API info set for BoostApp Step-Api.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authorization";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Please insert JWT token")
                                )
                )
                .addServersItem(new Server().url("/"))
                .info(apiInfo());
    }

    /**
     * Creates an {@link Info} object for BoostApp Step-Api.
     *
     * @return {@link Info} object with title, description, and version set for BoostApp Step-Api.
     */
    private Info apiInfo() {
        return new Info()
                .title("Step API Documentation")
                .description("Sigma BoostApp Step-api")
                .version("1.0");
    }
}

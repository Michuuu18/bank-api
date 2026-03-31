package pl.Pielichowski.bank_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankOpenAPI(@Value("${server.port:8081}") int serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank API")
                        .description("REST API systemu bankowego: użytkownicy, rachunki, transakcje. Specyfikacja OpenAPI 3.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pielichowski")
                                .email("dev@example.com"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server().url("http://localhost:" + serverPort).description("Lokalnie"));
    }
}

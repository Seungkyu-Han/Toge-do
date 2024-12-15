package vp.togedo.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class SwaggerConfig(
    @Value("\${SWAGGER.URL}")
    private val swaggerUrl: String
) {

    @Bean
    fun openApi(): OpenAPI {

        val securityScheme: SecurityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER).name("Authorization")

        val securityRequirement: SecurityRequirement = SecurityRequirement().addList("Bearer")

        return OpenAPI()
            .components(Components())
            .info(
                Info().apply {
                    title = "Toge-do 스케줄 서버"
                    description = "스케줄 웹 애플리케이션 서버입니다."
                }
            )
            .addServersItem(Server().url(swaggerUrl).description("Swagger API"))
            .components(
                Components().addSecuritySchemes("Bearer", securityScheme)
            )
            .security(
                Collections.singletonList(securityRequirement)
            )
    }
}
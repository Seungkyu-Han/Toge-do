package vp.togedo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun oauthTokenWebClient(): WebClient =
        WebClient.builder()
            .baseUrl("https://kauth.kakao.com/oauth/token")
            .defaultHeaders {
                it.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
            }
            .build()

}
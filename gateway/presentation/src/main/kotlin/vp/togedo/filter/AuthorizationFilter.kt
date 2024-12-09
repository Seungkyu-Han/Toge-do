package vp.togedo.filter

import io.jsonwebtoken.JwtException
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import vp.togedo.config.JwtTokenProvider
@Component
class AuthorizationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : AbstractGatewayFilterFactory<AuthorizationFilter.Config>(Config::class.java) {

    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val authHeader = exchange.request.headers["Authorization"]?.firstOrNull()

            if (!isValidAuthorizationHeader(authHeader)) {
                return@GatewayFilter forbid(exchange)
            }

            val token = authHeader!!.removePrefix("Bearer ")

            if (!jwtTokenProvider.isAccessToken(token)) {
                return@GatewayFilter forbid(exchange)
            }

            try {
                val userId = jwtTokenProvider.getUserId(token)
                val nextReq = exchange.request.mutate()
                    .header("X-VP-UserId", userId)
                    .build()

                chain.filter(exchange.mutate().request(nextReq).build())
            } catch (jwtException: JwtException) {
                forbid(exchange)
            }
        }
    }

    private fun isValidAuthorizationHeader(authHeader: String?): Boolean {
        return authHeader != null && authHeader.startsWith("Bearer ")
    }

    private fun forbid(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = HttpStatus.FORBIDDEN
        return exchange.response.setComplete()
    }
}

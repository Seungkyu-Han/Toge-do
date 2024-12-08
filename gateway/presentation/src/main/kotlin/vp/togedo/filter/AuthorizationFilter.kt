package vp.togedo.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import vp.togedo.config.JwtTokenProvider

@Component
class AuthorizationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) :
    AbstractGatewayFilterFactory<AuthorizationFilter.Config>(Config::class.java) {

    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->

            val authHeader = exchange.request.headers["Authorization"]?.firstOrNull()

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.response.statusCode = HttpStatus.FORBIDDEN
                exchange.response.setComplete()
            }
            else{

                val token = authHeader.removePrefix("Bearer ")

                if (jwtTokenProvider.isAccessToken(token)){
                    exchange.response.statusCode = HttpStatus.FORBIDDEN
                    exchange.response.setComplete()
                }
                else{
                    val userId = jwtTokenProvider.getUserId(token)

                    val nextReq = exchange.request.mutate()
                        .header("X-VP-UserId", userId)
                        .build()

                    chain.filter(exchange.mutate()
                        .request(nextReq)
                        .build())
                }
            }
        }
    }
}

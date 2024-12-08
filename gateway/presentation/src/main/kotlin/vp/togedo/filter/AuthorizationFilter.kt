package vp.togedo.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class AuthorizationFilter :
    AbstractGatewayFilterFactory<AuthorizationFilter.Config>(Config::class.java) {

    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->

            val authHeader = exchange.request.headers["Authorization"]?.firstOrNull()

            if (authHeader == null) {
                exchange.response.statusCode = HttpStatus.FORBIDDEN
                exchange.response.setComplete()
            }
            else{
                val nextReq = exchange.request.mutate()
                    .header("X-VP-UserId", authHeader)
                    .build()

                chain.filter(exchange.mutate()
                    .request(nextReq)
                    .build())
            }
        }
    }
}

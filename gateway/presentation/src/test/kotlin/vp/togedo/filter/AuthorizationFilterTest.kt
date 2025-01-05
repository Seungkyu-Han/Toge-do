package vp.togedo.filter


import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.security.config.JwtTokenProvider

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AuthorizationFilter::class])
class AuthorizationFilterTest{

    @MockBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var authorizationFilter: AuthorizationFilter

    @BeforeEach
    fun setUp() {
        authorizationFilter = AuthorizationFilter(jwtTokenProvider)
    }

    @Test
    @DisplayName("Authorization이 존재하지 않는 경우")
    fun emptyAuthorizationHeaderReturnForbidden(){
        //given
        val request = MockServerHttpRequest.get("/")
        val exchange = MockServerWebExchange.from(request)

        val filter = authorizationFilter.apply(AuthorizationFilter.Config())

        //when
        StepVerifier.create(filter.filter(exchange) { _ -> Mono.empty() })
            .verifyComplete()

        //then
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.response.statusCode)
    }

    @Test
    @DisplayName("Token이 Bearer로 시작하지 않는 경우")
    fun authorizationIsNotStartWithBearerReturnForbidden(){
        //given
        val request = MockServerHttpRequest.get("/")
            .header(HttpHeaders.AUTHORIZATION, "NOT BEARER!!")

        val exchange = MockServerWebExchange.from(request)
        val filter = authorizationFilter.apply(AuthorizationFilter.Config())

        //when
        StepVerifier.create(filter.filter(exchange) { _ -> Mono.empty() })
            .verifyComplete()

        //then
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.response.statusCode)
    }

    @Test
    @DisplayName("토큰이 만료된 경우")
    fun tokenIsExpiredReturnForbidden(){
        //given
        val token = "i am token!"
        val request = MockServerHttpRequest.get("/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")

        val exchange = MockServerWebExchange.from(request)
        val filter = authorizationFilter.apply(AuthorizationFilter.Config())

        `when`(jwtTokenProvider.getUserId(token))
            .thenThrow(ExpiredJwtException::class.java)

        //when
        StepVerifier.create(filter.filter(exchange) { _ -> Mono.empty() })
            .verifyComplete()

        //then
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.response.statusCode)
    }

    @Test
    @DisplayName("Jwt token이 아닌경우")
    fun tokenIsNotJwtReturnForbidden(){
        //given
        val token = "i am token!"
        val request = MockServerHttpRequest.get("/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")

        val exchange = MockServerWebExchange.from(request)
        val filter = authorizationFilter.apply(AuthorizationFilter.Config())

        `when`(jwtTokenProvider.getUserId(token))
            .thenThrow(MalformedJwtException::class.java)

        //when
        StepVerifier.create(filter.filter(exchange) { _ -> Mono.empty() })
            .verifyComplete()

        //then
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.response.statusCode)

    }

    @Test
    @DisplayName("secret key가 맞지 않는 경우")
    fun tokenUnmatchedSecretKeyReturnForbidden(){
        //given
        val token = "i am token!"
        val request = MockServerHttpRequest.get("/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")

        val exchange = MockServerWebExchange.from(request)
        val filter = authorizationFilter.apply(AuthorizationFilter.Config())

        `when`(jwtTokenProvider.getUserId(token))
            .thenThrow(SignatureException::class.java)

        //when
        StepVerifier.create(filter.filter(exchange) { _ -> Mono.empty() })
            .verifyComplete()

        //then
        Assertions.assertEquals(HttpStatus.FORBIDDEN, exchange.response.statusCode)
    }
}
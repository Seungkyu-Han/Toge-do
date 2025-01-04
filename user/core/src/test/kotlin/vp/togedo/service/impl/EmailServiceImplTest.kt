package vp.togedo.service.impl

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.util.ValidationUtil
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [EmailServiceImpl::class])
class EmailServiceImplTest {

    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    @SpyBean
    private lateinit var validationUtil: ValidationUtil

    private lateinit var emailService: EmailServiceImpl

    @BeforeEach
    fun setUp() {
        emailService = EmailServiceImpl(
            reactiveRedisTemplate = reactiveRedisTemplate,
            validationUtil = validationUtil
        )
    }



    @Nested
    inner class CheckValidEmail {

        private val redisKeyPrefix = "email:validation"

        @Test
        @DisplayName("유효한 코드를 입력시 true를 반환")
        fun checkValidCodeReturnTrue(){
            //given
            val email = "${UUID.randomUUID()}@test.com"
            val code = UUID.randomUUID().toString()

            //when
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisKeyPrefix:$email"))
                .thenReturn(Mono.just(code))

            //then
            StepVerifier.create(emailService.checkValidEmail(email, code))
                .expectNext(true)
                .verifyComplete()
        }

        @Test
        @DisplayName("유효하지 않은 코드를 입력시 true를 반환")
        fun checkInValidCodeReturnFalse(){
            //given
            val email = "${UUID.randomUUID()}@test.com"
            val code = UUID.randomUUID().toString()

            //when
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisKeyPrefix:$email"))
                .thenReturn(Mono.just(UUID.randomUUID().toString()))

            //then
            StepVerifier.create(emailService.checkValidEmail(email, code))
                .expectNext(false)
                .verifyComplete()
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 요청시 false를 반환")
        fun checkInValidEmailReturnFalse(){
            //given
            val email = "${UUID.randomUUID()}@test.com"
            val code = UUID.randomUUID().toString()

            //when
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisKeyPrefix:$email"))
                .thenReturn(Mono.empty())

            //then
            StepVerifier.create(emailService.checkValidEmail(email, code))
                .expectNext(false)
                .verifyComplete()
        }
    }
}
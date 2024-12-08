package vp.togedo.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.RecordMetadata
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
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import reactor.test.StepVerifier
import vp.togedo.util.ValidationUtil
import java.time.Duration
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [EmailServiceImpl::class])
class EmailServiceImplTest {

    @MockBean
    private lateinit var reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>

    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    @SpyBean
    private lateinit var objectMapper: ObjectMapper

    @SpyBean
    private lateinit var validationUtil: ValidationUtil

    private lateinit var emailService: EmailServiceImpl

    @BeforeEach
    fun setUp() {
        emailService = EmailServiceImpl(
            reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate,
            reactiveRedisTemplate = reactiveRedisTemplate,
            objectMapper = objectMapper,
            validationUtil = validationUtil
        )
    }


    @Nested
    inner class SendValidationCode {
        private val sendEmailValidationCodeTopic = "SEND_EMAIL_VALIDATION_CODE_TOPIC"

        @Test
        @DisplayName("kafka로 이벤트 전달 확인")
        fun sendEmailToKafkaReturnSuccess(){
            //given
            val email = "${UUID.randomUUID()}@test.com"

            val recordMetadata = RecordMetadata(null, 0, 0, 0, 0, 0)

            // SenderResult 구현
            val senderResult = object : SenderResult<Void> {
                override fun recordMetadata(): RecordMetadata = recordMetadata
                override fun exception(): Exception? = null
                override fun correlationMetadata(): Void? = null
            }

            `when`(reactiveKafkaProducerTemplate.send(eq(sendEmailValidationCodeTopic), anyString()))
                .thenReturn(Mono.just(senderResult))

            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.set(anyString(), anyString(), any<Duration>()))
                .thenReturn(Mono.just(true))

            //when & then
            StepVerifier.create(emailService.sendValidationCode(email))
                .expectNextCount(1)
                .verifyComplete()

            verify(reactiveKafkaProducerTemplate, times(1)).send(eq(sendEmailValidationCodeTopic), anyString())
        }
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
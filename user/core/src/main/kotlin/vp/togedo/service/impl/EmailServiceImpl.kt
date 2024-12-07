package vp.togedo.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.data.email.ValidCodeEventDto
import vp.togedo.service.EmailService
import vp.togedo.util.ValidationUtil
import java.time.Duration

@Service
class EmailServiceImpl(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val validationUtil: ValidationUtil
): EmailService {

    private val sendEmailValidationCodeTopic = "SEND_EMAIL_VALIDATION_CODE_TOPIC"

    override fun sendValidationCode(email: String): Mono<SenderResult<Void>> {
        val code = validationUtil.verificationCode()
        val eventMessage = ValidCodeEventDto(email = email, code = code)

        return reactiveRedisTemplate.opsForValue()
            .set("email:validation:$email", code, Duration.ofMinutes(5))
            .flatMap {
                reactiveKafkaProducerTemplate.send(
                    sendEmailValidationCodeTopic,
                    objectMapper.writeValueAsString(eventMessage)
                )
            }
    }


    override fun checkValidEmail(email: String, code: String): Mono<Boolean> =
        reactiveRedisTemplate
            .opsForValue().get("email:validation:$email")
            .map { it == code }
            .defaultIfEmpty(false)

}
package vp.togedo.service.impl

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.service.EmailService

@Service
class EmailServiceImpl(
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
): EmailService {

    private val sendEmailValidationCodeTopic = "SEND_EMAIL_VALIDATION_CODE_TOPIC"

    override fun sendValidationCode(email: String): Mono<SenderResult<Void>> =
        reactiveKafkaProducerTemplate.send(sendEmailValidationCodeTopic, email)


    override fun checkValidEmail(email: String, code: String): Mono<Boolean> =
        reactiveRedisTemplate
            .opsForValue().get("email:validation:$email")
            .map { it == code }
            .defaultIfEmpty(false)

}
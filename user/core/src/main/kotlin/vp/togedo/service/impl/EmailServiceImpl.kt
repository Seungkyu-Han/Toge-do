package vp.togedo.service.impl

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.service.EmailService
import vp.togedo.util.ValidationUtil
import java.time.Duration

@Service
class EmailServiceImpl(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val validationUtil: ValidationUtil
): EmailService {

    override fun createValidationCode(email: String): Mono<String> {
        val code = validationUtil.verificationCode()
        return reactiveRedisTemplate.opsForValue()
            .set("email:validation:$email", code, Duration.ofMinutes(5))
            .map{code}
    }

    override fun checkValidEmail(email: String, code: String): Mono<Boolean> =
        reactiveRedisTemplate
            .opsForValue().get("email:validation:$email")
            .map { it == code }
            .defaultIfEmpty(false)

}
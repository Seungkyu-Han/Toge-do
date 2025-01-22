package vp.togedo.redis.service.impl

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.redis.service.ValidCodeService
import java.time.Duration

@Service
class ValidCodeServiceImpl(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
): ValidCodeService {

    val validCodePrefix = "email:validation:"

    override fun saveCodeByEmail(email: String, code: String): Mono<Void> =
        reactiveRedisTemplate.opsForValue()
            .set(validCodePrefix + email, code, Duration.ofMinutes(5))
            .then()


    override fun findCodeByEmail(email: String): Mono<String> =
        reactiveRedisTemplate.opsForValue().get(validCodePrefix + email)
}
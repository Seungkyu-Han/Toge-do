package vp.togedo.redis.service.impl

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.redis.service.DeviceTokenService

@Service
class DeviceTokenServiceImpl(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
): DeviceTokenService {

    val deviceTokenPrefix = "deviceToken:"

    override fun saveDeviceToken(id: String, deviceToken: String): Mono<Void> =
        reactiveRedisTemplate.opsForValue()
            .set(deviceTokenPrefix + id, deviceToken)
            .then()

    override fun findById(id: String): Mono<String> =
        reactiveRedisTemplate.opsForValue()
            .get(deviceTokenPrefix + id)

    override fun deleteById(id: String): Mono<Void> =
        reactiveRedisTemplate.opsForValue()
            .delete(deviceTokenPrefix + id)
            .then()
}
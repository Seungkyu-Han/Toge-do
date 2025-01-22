package vp.togedo.redis.service

import reactor.core.publisher.Mono

interface DeviceTokenService {

    fun saveDeviceToken(id: String, deviceToken: String): Mono<Void>

    fun findById(id: String): Mono<String>

    fun deleteById(id: String): Mono<Void>
}
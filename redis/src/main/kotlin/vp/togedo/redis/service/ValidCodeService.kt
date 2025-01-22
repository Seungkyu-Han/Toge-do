package vp.togedo.redis.service

import reactor.core.publisher.Mono

interface ValidCodeService {

    fun saveCodeByEmail(email: String, code: String): Mono<Void>

    fun findCodeByEmail(email: String): Mono<String>
}
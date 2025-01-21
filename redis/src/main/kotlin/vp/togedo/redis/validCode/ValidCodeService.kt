package vp.togedo.redis.validCode

import reactor.core.publisher.Mono

interface ValidCodeService {

    fun saveCodeByEmail(email: String, code: String): Mono<Void>

    fun findCodeByEmail(email: String): Mono<String>
}
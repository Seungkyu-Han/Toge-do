package vp.togedo.service

import reactor.core.publisher.Mono

interface EmailService {

    fun createValidationCode(email: String): Mono<String>

    fun checkValidEmail(email: String, code: String): Mono<Boolean>
}
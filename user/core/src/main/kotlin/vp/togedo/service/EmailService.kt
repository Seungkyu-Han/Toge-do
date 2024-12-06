package vp.togedo.service

import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult

interface EmailService {

    fun sendValidationCode(email: String): Mono<SenderResult<Void>>

    fun checkValidEmail(email: String, code: String): Mono<Boolean>
}
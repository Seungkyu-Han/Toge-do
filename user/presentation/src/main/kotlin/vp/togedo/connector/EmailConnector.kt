package vp.togedo.connector

import reactor.core.publisher.Mono


interface EmailConnector {

    fun requestValidCode(email: String): Mono<Void>

    fun checkValidCode(code: String, email: String): Mono<Boolean>
}
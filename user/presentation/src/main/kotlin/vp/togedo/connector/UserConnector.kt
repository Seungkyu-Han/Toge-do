package vp.togedo.connector

import reactor.core.publisher.Mono
import vp.togedo.dto.KakaoLoginRes

interface UserConnector {

    fun login(
        code: String
    ): Mono<KakaoLoginRes>
}
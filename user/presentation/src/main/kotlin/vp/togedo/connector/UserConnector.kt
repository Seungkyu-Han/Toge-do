package vp.togedo.connector

import reactor.core.publisher.Mono
import vp.togedo.dto.KakaoLoginRes

interface UserConnector {

    fun kakaoLogin(
        code: String
    ): Mono<KakaoLoginRes>
}
package vp.togedo.connector

import reactor.core.publisher.Mono
import vp.togedo.dto.LoginRes

interface UserConnector {

    fun kakaoLogin(
        code: String
    ): Mono<LoginRes>

    fun reissueAccessToken(
        refreshToken: String
    ): LoginRes
}
package vp.togedo.service

import reactor.core.publisher.Mono
import vp.togedo.data.dto.google.GoogleAccessToken
import vp.togedo.data.dto.google.GoogleUserInfo

interface GoogleService {

    fun getGoogleAccessToken(code: String): Mono<GoogleAccessToken>

    fun getGoogleUserInfo(accessToken: String): Mono<GoogleUserInfo>
}
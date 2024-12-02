package vp.togedo.service

import reactor.core.publisher.Mono

interface KakaoService {

    fun oauthToken(code: String, redirectUri: String): Mono<String>
}
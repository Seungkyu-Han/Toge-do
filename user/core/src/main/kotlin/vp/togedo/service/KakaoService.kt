package vp.togedo.service

import reactor.core.publisher.Mono
import vp.togedo.data.dto.kakao.OauthLogout
import vp.togedo.data.dto.kakao.OauthTokenRes
import vp.togedo.data.dto.kakao.V2UserMe

interface KakaoService {

    fun oauthToken(code: String, redirectUri: String): Mono<OauthTokenRes>

    fun v2UserMe(accessToken: String): Mono<V2UserMe>

    fun oauthLogout(accessToken: String): Mono<OauthLogout>
}
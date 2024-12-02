package vp.togedo.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import vp.togedo.data.dto.kakao.*
import vp.togedo.service.KakaoService

@Service
class KakaoServiceImpl(
    @Value("\${KAKAO.REST_API_KEY}")
    val kakaoApiKey: String
): KakaoService {

    override fun oauthToken(code: String, redirectUri: String): Mono<OauthTokenRes> =
        WebClient.builder()
            .baseUrl("https://kauth.kakao.com/oauth/token")
            .defaultHeaders {
                it.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
            }
            .build()
            .post()
            .body(
                BodyInserters.fromValue(
                    OauthTokenReq(
                        code = code,
                        redirectUri = redirectUri,
                        clientId = kakaoApiKey
                    )
                )
            )
            .retrieve()
            .bodyToMono(
                OauthTokenRes::class.java
            )

    override fun v2UserMe(accessToken: String): Mono<V2UserMe> =
         WebClient.create()
            .post().uri("https://kapi.kakao.com/v2/user/me")
            .headers {
                it.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                it.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
            }
            .retrieve()
            .bodyToMono(V2UserMe::class.java)

    override fun oauthLogout(accessToken: String): Mono<OauthLogout> =
        WebClient.create()
            .post().uri("https://kapi.kakao.com/v1/user/logout")
            .headers {
                it.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            }
            .retrieve()
            .bodyToMono(OauthLogout::class.java)

    override fun v1UserUnlink(accessToken: String): Mono<V1UserUnlink> =
        WebClient.create()
            .post().uri("https://kapi.kakao.com/v1/user/unlink")
            .headers{
                it.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            }
            .retrieve()
            .bodyToMono(V1UserUnlink::class.java)

}
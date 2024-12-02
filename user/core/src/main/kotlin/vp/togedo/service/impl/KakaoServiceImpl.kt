package vp.togedo.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import vp.togedo.data.dto.kakao.OauthTokenReq
import vp.togedo.data.dto.kakao.OauthTokenRes
import vp.togedo.service.KakaoService

@Service
class KakaoServiceImpl(
    @Value("\${KAKAO.REST_API_KEY}")
    val kakaoApiKey: String,
    val oauthTokenWebClient: WebClient,
): KakaoService {

    override fun oauthToken(code: String, redirectUri: String): Mono<String> =
         oauthTokenWebClient.post()
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
            .map {
                it.accessToken
            }
}
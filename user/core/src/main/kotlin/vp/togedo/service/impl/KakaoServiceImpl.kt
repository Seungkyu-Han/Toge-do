package vp.togedo.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import vp.togedo.data.dto.kakao.*
import vp.togedo.service.KakaoService

@Service
class KakaoServiceImpl(
    @Value("\${KAKAO.REST_API_KEY}")
    val kakaoApiKey: String
): KakaoService {

    /**
     * 카카오 인증서버로부터 Oauth 토큰을 요청
     * @author Seungkyu-Han
     * @param code 카카오 로그인 코드
     * @param redirectUri 카카오 redirect 주소
     * @return 카카오 Oauth 토큰
     */
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

    /**
     * 카카오 인증서버로부터 사용자 정보를 요청
     * @author Seungkyu-Han
     * @param accessToken 카카오 인증서버로부터 응답받은 access token
     * @return 카카오 유저 정보
     */
    override fun v2UserMe(accessToken: String): Mono<V2UserMe> =
         WebClient.create()
            .post().uri("https://kapi.kakao.com/v2/user/me")
            .headers {
                it.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                it.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
            }
            .retrieve()
            .bodyToMono(V2UserMe::class.java)

    /**
     * 카카오 인증서버로부터 받은 토큰들을 삭제
     * @param accessToken 카카오 인증서버로부터 응답받은 access token
     * @return 카카오 유저 아이디
     */
    override fun oauthLogout(accessToken: String): Mono<OauthLogout> =
        WebClient.create()
            .post().uri("https://kapi.kakao.com/v1/user/logout")
            .headers {
                it.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            }
            .retrieve()
            .bodyToMono(OauthLogout::class.java)

    /**
     * 카카오 회원 탈퇴
     * @param accessToken 카카오 인증서버로부터 응답받은 access token
     * @return 카카오 유저 아이디
     */
    override fun v1UserUnlink(accessToken: String): Mono<V1UserUnlink> =
        WebClient.create()
            .post().uri("https://kapi.kakao.com/v1/user/unlink")
            .headers{
                it.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            }
            .retrieve()
            .bodyToMono(V1UserUnlink::class.java)

}
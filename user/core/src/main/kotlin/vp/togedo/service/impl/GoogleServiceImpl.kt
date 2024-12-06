package vp.togedo.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import vp.togedo.data.dto.google.GoogleAccessToken
import vp.togedo.data.dto.google.GoogleUserInfo
import vp.togedo.service.GoogleService

@Service
class GoogleServiceImpl(
    @Value("\${GOOGLE.AUTH.CLIENT_ID}")
    private val clientId: String,
    @Value("\${GOOGLE.AUTH.CLIENT_SECRET}")
    private val clientSecret: String,
    @Value("\${GOOGLE.AUTH.REDIRECT_URI}")
    private val redirectUri: String
): GoogleService {

    /**
     * 구글 인증서버로부터 access token을 요청
     * @author Seungkyu-Han
     * @param code 구글 로그인 코드
     * @return 구글 access token
     */
    override fun getGoogleAccessToken(code: String): Mono<GoogleAccessToken> =
        WebClient.create()
            .post().uri("https://oauth2.googleapis.com/token")
            .headers{
                it.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
            }
            .body(
                BodyInserters.fromFormData("client_id", clientId)
                    .with("code", code)
                    .with("client_secret", clientSecret)
                    .with("redirect_uri", redirectUri)
                    .with("grant_type", "authorization_code")
            ).retrieve()
            .bodyToMono(GoogleAccessToken::class.java)

    /**
     * 구글 인증서버로부터 사용자 정보를 요청
     * @author Seungkyu-Han
     * @param accessToken 구글 access token
     * @return 구글 사용자 정보
     */
    override fun getGoogleUserInfo(accessToken: String): Mono<GoogleUserInfo> =
        WebClient.create()
            .get().uri("https://www.googleapis.com/oauth2/v2/userinfo")
            .headers {
                it.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                it.set(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            }
            .retrieve()
            .bodyToMono(GoogleUserInfo::class.java)

}
package vp.togedo.connector.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.connector.UserConnector
import vp.togedo.dto.KakaoLoginRes
import vp.togedo.enums.OauthEnum
import vp.togedo.service.KakaoService
import vp.togedo.service.UserService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException

@Service
class UserConnectorImpl(
    private val userService: UserService,
    private val kakaoService: KakaoService
): UserConnector {

    override fun login(
        code: String): Mono<KakaoLoginRes> =
        kakaoService.oauthToken(code)
            .flatMap{
                kakaoService.v2UserMe(it.accessToken)
            }
            .flatMap {
                v2UserMe ->
                userService.getUserInfoByOauth(
                    oauthEnum = OauthEnum.KAKAO,
                    kakaoId = v2UserMe.id
                ).onErrorResume {
                    if(it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND_BY_OAUTH) {
                        userService.createUser(
                            oauthEnum = OauthEnum.KAKAO,
                            kakaoId = v2UserMe.id
                        )
                    }else{
                        throw UserException(ErrorCode.LOGIN_UNEXPECTED_ERROR)
                    }
                }
            }
            .map {
                KakaoLoginRes(
                    accessToken = userService.createJwtAccessToken(it.id!!),
                    refreshToken = userService.createJwtAccessToken(it.id!!)
                )
            }

}
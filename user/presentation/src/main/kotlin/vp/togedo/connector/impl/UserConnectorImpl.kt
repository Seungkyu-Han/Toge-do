package vp.togedo.connector.impl

import io.jsonwebtoken.MalformedJwtException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.connector.UserConnector
import vp.togedo.dto.LoginRes
import vp.togedo.enums.OauthEnum
import vp.togedo.service.ImageService
import vp.togedo.service.KakaoService
import vp.togedo.service.UserService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException

@Service
class UserConnectorImpl(
    private val userService: UserService,
    private val kakaoService: KakaoService,
    private val imageService: ImageService
): UserConnector {

    override fun kakaoLogin(
        code: String): Mono<LoginRes> =
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
                            kakaoId = v2UserMe.id,
                            name = v2UserMe.kakaoAccount?.name,
                            email = v2UserMe.kakaoAccount?.email,
                            profileImageUrl = v2UserMe.kakaoAccount?.profile?.profileImageUrl
                        )
                    }else{
                        throw UserException(ErrorCode.LOGIN_UNEXPECTED_ERROR)
                    }
                }
            }
            .map {
                LoginRes(
                    accessToken = userService.createJwtAccessToken(it.id!!),
                    refreshToken = userService.createJwtAccessToken(it.id!!)
                )
            }

    override fun reissueAccessToken(refreshToken: String): LoginRes {
        return try{
            LoginRes(
                userService.createJwtAccessToken(
                    userService.getUserIdByToken(refreshToken.removePrefix("Bearer "))
                ),
                refreshToken = refreshToken
            )
        }catch (malformedJwtException: MalformedJwtException){
            throw UserException(ErrorCode.INVALID_TOKEN)
        }catch (illegalArgumentException: IllegalArgumentException){
            throw UserException(ErrorCode.LOGIN_UNEXPECTED_ERROR)
        }
    }
}
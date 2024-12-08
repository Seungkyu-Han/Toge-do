package vp.togedo.connector.impl

import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.connector.UserConnector
import vp.togedo.dto.LoginRes
import vp.togedo.dto.UserInfoReqDto
import vp.togedo.dto.UserInfoResDto
import vp.togedo.enums.OauthEnum
import vp.togedo.service.GoogleService
import vp.togedo.service.ImageService
import vp.togedo.service.KakaoService
import vp.togedo.service.UserService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException

@Service
class UserConnectorImpl(
    private val userService: UserService,
    private val kakaoService: KakaoService,
    private val googleService: GoogleService,
    private val imageService: ImageService
): UserConnector {

    override fun extractUserIdByToken(token: String): ObjectId{
        if(!token.startsWith("Bearer"))
            throw UserException(ErrorCode.INVALID_TOKEN)
        return try{
            userService.getUserIdByToken(token.removePrefix("Bearer "))
        } catch ( signatureException: SignatureException){
            throw UserException(ErrorCode.INVALID_TOKEN)
        } catch (malformedJwtException: MalformedJwtException){
            throw UserException(ErrorCode.INVALID_TOKEN)
        } catch (illegalArgumentException: IllegalArgumentException){
            throw UserException(ErrorCode.INVALID_TOKEN)
        }
    }

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

    override fun googleLogin(code: String): Mono<LoginRes> =
        googleService.getGoogleAccessToken(code)
            .flatMap {
                googleService.getGoogleUserInfo(it.accessToken)
            }
            .flatMap {
                googleUserInfo ->
                userService.getUserInfoByOauth(
                    oauthEnum = OauthEnum.GOOGLE,
                    googleId = googleUserInfo.id
                ).onErrorResume {
                        if(it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND_BY_OAUTH) {
                            userService.createUser(
                                oauthEnum = OauthEnum.GOOGLE,
                                googleId = googleUserInfo.id,
                                name = googleUserInfo.name,
                                email = googleUserInfo.email,
                                profileImageUrl = googleUserInfo.picture
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
                    this.extractUserIdByToken(refreshToken)
                ),
                refreshToken = refreshToken
            )
        }catch (malformedJwtException: MalformedJwtException){
            throw UserException(ErrorCode.INVALID_TOKEN)
        }catch (illegalArgumentException: IllegalArgumentException){
            throw UserException(ErrorCode.LOGIN_UNEXPECTED_ERROR)
        }
    }

    override suspend fun updateUserInfo(userInfoReqDto: UserInfoReqDto, id: ObjectId): UserInfoResDto {
        val userDocument = userService.findUser(id).awaitSingle()

        if (userDocument.profileImageUrl != null){
            val fileName = userDocument.profileImageUrl!!.split("/").last()
            imageService.publishDeleteEvent(fileName).awaitSingleOrNull()
        }

        if (userInfoReqDto.image != null){
            val image = imageService.saveImage(userInfoReqDto.image).awaitSingle()
            userDocument.profileImageUrl = image
        }

        userDocument.name = userInfoReqDto.name
        userDocument.email = userInfoReqDto.email

        return userService.saveUser(userDocument)
            .map{
                UserInfoResDto(it)
            }
            .awaitSingle()
    }

    override suspend fun findUserInfo(id: ObjectId): UserInfoResDto {
        return userService.findUser(id)
            .map{
                UserInfoResDto(it)
            }
            .awaitSingle()
    }
}
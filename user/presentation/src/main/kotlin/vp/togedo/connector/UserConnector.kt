package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.dto.LoginRes
import vp.togedo.dto.UserInfoReqDto
import vp.togedo.dto.UserInfoResDto

interface UserConnector {

    fun kakaoLogin(
        code: String
    ): Mono<LoginRes>

    fun googleLogin(
        code: String
    ): Mono<LoginRes>

    fun reissueAccessToken(
        refreshToken: String
    ): LoginRes

    suspend fun updateUserInfo(
        userInfoReqDto: UserInfoReqDto,
        id: ObjectId
    ): UserInfoResDto

    suspend fun findUserInfo(
        id: ObjectId
    ): UserInfoResDto

    fun extractUserIdByToken(token: String?): ObjectId
}
package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.dto.user.LoginRes
import vp.togedo.dto.user.UserInfoReqDto
import vp.togedo.dto.user.UserInfoResDto
import vp.togedo.model.documents.user.UserDocument

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

    fun changeNotification(
        isAgree: Boolean,
        deviceToken: String,
        id: ObjectId
    ): Mono<UserDocument>
}
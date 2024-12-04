package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.document.UserDocument
import vp.togedo.enums.OauthEnum

interface UserService {

    fun createJwtAccessToken(id: ObjectId): String

    fun createJwtRefreshToken(id: ObjectId): String

    fun getUserIdByToken(token: String): ObjectId

    fun getUserInfoByOauth(
        oauthEnum: OauthEnum,
        kakaoId: Long? = null,
        googleId: String? = null): Mono<UserDocument>

    fun createUser(
        oauthEnum: OauthEnum,
        kakaoId: Long? = null,
        googleId: String? = null,
        name: String? = null,
        email: String? = null): Mono<UserDocument>

}
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
        googleId: Long? = null): Mono<UserDocument>

    fun createUser(
        oauthEnum: OauthEnum,
        kakaoId: Long? = null,
        googleId: Long? = null,
        name: String? = null,
        email: String? = null,
        profileImageUrl: String? = null): Mono<UserDocument>

    fun findUser(id: ObjectId): Mono<UserDocument>

    fun saveUser(userDocument: UserDocument): Mono<UserDocument>

}
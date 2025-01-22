package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.enums.OauthEnum
import vp.togedo.model.documents.user.UserDocument

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
        email: String? = null,
        profileImageUrl: String? = null): Mono<UserDocument>

    fun insertOauthIdByEmail(
        oauthEnum: OauthEnum,
        kakaoId: Long? = null,
        googleId: String? = null,
        email: String
    ): Mono<UserDocument>

    fun findUser(id: ObjectId): Mono<UserDocument>

    fun findUserByEmail(email: String): Mono<UserDocument>

    fun saveUser(userDocument: UserDocument): Mono<UserDocument>
}
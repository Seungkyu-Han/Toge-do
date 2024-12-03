package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.document.UserDocument
import vp.togedo.enums.OauthEnum

interface UserService {

    fun createJwtAccessToken(id: ObjectId): String

    fun createJwtRefreshToken(id: ObjectId): String

    fun getUserInfoByOauth(
        oauthEnum: OauthEnum,
        kakaoId: Long? = null,
        googleId: String? = null): Mono<UserDocument>
}
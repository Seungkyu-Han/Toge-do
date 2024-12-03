package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import vp.togedo.UserRepository
import vp.togedo.config.jwt.JwtTokenProvider
import vp.togedo.document.Oauth
import vp.togedo.document.UserDocument
import vp.togedo.enums.OauthEnum
import vp.togedo.service.UserService

@Service
class UserServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
): UserService {

    override fun createJwtAccessToken(id: ObjectId): String {
        val userId = id.toHexString()
        return jwtTokenProvider.getAccessToken(userId)
    }

    override fun createJwtRefreshToken(id: ObjectId): String {
        val userId = id.toHexString()
        return jwtTokenProvider.getAccessToken(userId)
    }

    @Transactional(readOnly = true)
    override fun getUserInfoByOauth(
        oauthEnum: OauthEnum,
        kakaoId: Long?,
        googleId: String?): Mono<UserDocument> {

        val oauth = Oauth(
            oauthType = oauthEnum,
            kakaoId = kakaoId,
            googleId = googleId
        )

        return userRepository.findByOauth(oauth)
    }

    @Transactional
    override fun createUser(
        oauthEnum: OauthEnum,
        kakaoId: Long?,
        googleId: String?): Mono<UserDocument> {

        val oauth = Oauth(
            oauthType = oauthEnum,
            kakaoId = kakaoId,
            googleId = googleId
        )

        val user = UserDocument(
            id = null,
            oauth = oauth
        )

        return userRepository.save(user)
    }
}
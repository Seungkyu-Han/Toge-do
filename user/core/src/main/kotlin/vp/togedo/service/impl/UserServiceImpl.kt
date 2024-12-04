package vp.togedo.service.impl

import io.jsonwebtoken.MalformedJwtException
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
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException

@Service
class UserServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
): UserService {

    /**
     * 유저의 objectId를 사용하여 access token 생성
     * @param id 유저의 objectId
     * @return 2시간 유효의 access token
     */
    override fun createJwtAccessToken(id: ObjectId): String {
        val userId = id.toHexString()
        return jwtTokenProvider.getAccessToken(userId)
    }

    /**
     * 유저의 objectId를 사용하여 refresh token 생성
     * @param id 유저의 objectId
     * @return 7일 유효의 refresh token
     */
    override fun createJwtRefreshToken(id: ObjectId): String {
        val userId = id.toHexString()
        return jwtTokenProvider.getRefreshToken(userId)
    }

    /**
     * Oauth로부터 받은 정보로 사용자를 조회
     * @param oauthEnum kakao, google
     * @param kakaoId kakao oauth라면 kakao의 유저아이디
     * @param googleId google oauth라면 google의 유저아이디
     * @return 유저 정보
     */
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
            .switchIfEmpty(
                Mono.error(
                    UserException(ErrorCode.USER_NOT_FOUND_BY_OAUTH)
                )
            )
    }

    /**
     * Oauth를 사용하여 사용자를 생성
     * @param oauthEnum kakao, google
     * @param kakaoId kakao oauth라면 kakao의 유저아이디
     * @param googleId google oauth라면 google의 유저아이디
     * @return 유저 정보
     */
    @Transactional
    override fun createUser(
        oauthEnum: OauthEnum,
        kakaoId: Long?,
        googleId: String?,
        name: String?,
        email: String?): Mono<UserDocument>{

    val oauth = Oauth(
            oauthType = oauthEnum,
            kakaoId = kakaoId,
            googleId = googleId
        )

        val user = UserDocument(
            id = null,
            oauth = oauth,
            name = name,
            email = email
        )

        return userRepository.save(user)
    }

    /**
     * Token에서 UserId를 추출
     * @param token 해당 서비스에서 발급받은 토큰
     * @return 해당 유저의 ObjectId
     * @throws MalformedJwtException 유효하지 않은 토큰 사용시 발생
     * @throws IllegalArgumentException ObjectId로 바꿀 수 없을 때 발생
     */
    override fun getUserIdByToken(token: String): ObjectId {
        return ObjectId(jwtTokenProvider.getUserId(token))
    }
}
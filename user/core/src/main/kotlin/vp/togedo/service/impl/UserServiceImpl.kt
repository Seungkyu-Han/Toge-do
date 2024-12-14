package vp.togedo.service.impl

import io.jsonwebtoken.MalformedJwtException
import org.bson.types.ObjectId
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import vp.togedo.repository.UserRepository
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

        return (if (oauthEnum == OauthEnum.KAKAO)
                userRepository.findByOauth_KakaoId(kakaoId!!)
            else
                userRepository.findByOauth_GoogleId(googleId!!)
                )
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
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @param profileImageUrl 사용자 프로필 이미지 url
     * @return 유저 정보
     * @throws DuplicateKeyException 해당 이메일이 이미 존재하는 경우
     */
    @Transactional
    override fun createUser(
        oauthEnum: OauthEnum,
        kakaoId: Long?,
        googleId: String?,
        name: String?,
        email: String?,
        profileImageUrl: String?): Mono<UserDocument>{

    val oauth = Oauth(
            kakaoId = kakaoId,
            googleId = googleId
        )

        val user = UserDocument(
            id = null,
            oauth = oauth,
            name = name ?: "사용자${(100..999).random()}",
            email = email,
            profileImageUrl = profileImageUrl
        )

        return userRepository.save(user)
            .onErrorResume{
                if(it is DuplicateKeyException){
                    insertOauthIdByEmail(
                        oauthEnum = oauthEnum,
                        kakaoId = kakaoId,
                        googleId = googleId,
                        email = email!!
                    )
                }else{
                    throw it
                }
            }
    }

    /**
     * email 중복이면 해당 계정에 Oauth 추가
     * @param oauthEnum oauth Type
     * @param kakaoId 카카오 oauth Id
     * @param googleId 구글 oauth Id
     * @param email 이메일
     * @return 유저 정보
     */
    @Transactional
    override fun insertOauthIdByEmail(
        oauthEnum: OauthEnum,
        kakaoId: Long?,
        googleId: String?,
        email: String
    ): Mono<UserDocument> {
        return userRepository.findByEmail(email)
            .flatMap{
                if (oauthEnum == OauthEnum.KAKAO) {
                    it.oauth.kakaoId = kakaoId
                }
                else{
                    it.oauth.googleId = googleId
                }
                userRepository.save(it)
            }
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

    /**
     * 사용자의 정보를 데이터베이스로부터 조회
     * @param id 사용자의 objectId
     * @return 사용자의 document
     */
    override fun findUser(id: ObjectId): Mono<UserDocument> {
        return userRepository.findById(id)
            .switchIfEmpty(
                Mono.error(UserException(ErrorCode.USER_NOT_FOUND))
            )
    }

    /**
     * 조회한 사용자의 정보를 데이터베이스에 저장
     * @param userDocument 저장할 UserDocument
     * @return 저장된 UserDocument
     */
    override fun saveUser(userDocument: UserDocument): Mono<UserDocument> {
        return userRepository.save(userDocument)
    }

    /**
     * 사용자의 정보를 이메일을 사용하여 데이터베이스로부터 조회
     * @param email 사용자의 정보를 가져올 이메일
     * @return 검색된 사용자의 user document
     * @throws UserException 해당 사용자가 존재하지 않는 경우
     */
    override fun findUserByEmail(email: String): Mono<UserDocument> {
        return userRepository.findByEmail(email)
            .switchIfEmpty(
                Mono.error(UserException(ErrorCode.USER_NOT_FOUND))
            )
    }
}
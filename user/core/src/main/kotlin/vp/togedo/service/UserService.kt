package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.enums.OauthEnum
import vp.togedo.model.documents.user.UserDocument
import vp.togedo.util.error.exception.UserException

interface UserService {

    /**
     * oauth로 사용자를 검색
     * @param oauthEnum oauth의 종류
     * @param kakaoId kakao oauth id
     * @param googleId google oauth id
     * @return 해당 사용자의 user document
     * @exception UserException 해당 사용자가 존재하지 않는 경우
     */
    fun getUserInfoByOauth(
        oauthEnum: OauthEnum,
        kakaoId: Long? = null,
        googleId: String? = null): Mono<UserDocument>

    /**
     * 해당 사용자 정보로 새로운 유저를 생성
     * @param oauthEnum 가입하는 oauth의 종류
     * @param kakaoId kakao oauth id
     * @param googleId google oauth id
     * @param name 사용자의 이름
     * @param email 회원가입하는 사용자의 이메일
     * @param profileImageUrl 사용자의 프로필 이미지
     */
    fun createUser(
        oauthEnum: OauthEnum,
        kakaoId: Long? = null,
        googleId: String? = null,
        name: String? = null,
        email: String? = null,
        profileImageUrl: String? = null): Mono<UserDocument>

    fun findUser(id: ObjectId): Mono<UserDocument>

    fun findUserByEmail(email: String): Mono<UserDocument>

    fun saveUser(userDocument: UserDocument): Mono<UserDocument>
}
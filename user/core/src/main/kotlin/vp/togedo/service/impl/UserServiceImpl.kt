package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import vp.togedo.repository.UserRepository
import vp.togedo.enums.OauthEnum
import vp.togedo.model.documents.user.Oauth
import vp.togedo.model.documents.user.UserDocument
import vp.togedo.service.UserService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
): UserService {

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


    @Transactional
    override fun createUser(
        oauthEnum: OauthEnum,
        kakaoId: Long?,
        googleId: String?,
        name: String?,
        email: String?,
        profileImageUrl: String?): Mono<UserDocument>{

        val userDocument = Mono.defer {
            Mono.just(
                UserDocument(
                    oauth = Oauth(
                        kakaoId = kakaoId,
                        googleId = googleId
                    ),
                    name = name ?: "사용자${(100..999).random()}",
                    email = email,
                    profileImageUrl = profileImageUrl
                )
            )
        }

        return if (email != null){
            return userRepository.findByEmail(email)
                .switchIfEmpty(
                    userDocument
                )
                .flatMap{
                    it.oauth.kakaoId = it.oauth.kakaoId ?: kakaoId
                    it.oauth.googleId = it.oauth.googleId ?: googleId
                    userRepository.save(it)
                }
        }
        else{
            userDocument.flatMap {
                userRepository.save(it)
            }
        }
    }

    override fun findUser(id: ObjectId): Mono<UserDocument> {
        return userRepository.findById(id)
            .switchIfEmpty(
                Mono.error(UserException(ErrorCode.USER_NOT_FOUND))
            )
    }

    override fun findUserByEmail(email: String): Mono<UserDocument> {
        return userRepository.findByEmail(email)
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

}
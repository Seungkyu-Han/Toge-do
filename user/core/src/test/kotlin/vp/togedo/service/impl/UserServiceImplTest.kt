package vp.togedo.service.impl

import io.jsonwebtoken.MalformedJwtException
import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.dao.DuplicateKeyException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono

import reactor.test.StepVerifier
import vp.togedo.UserRepository
import vp.togedo.config.jwt.JwtTokenProvider
import vp.togedo.document.Oauth
import vp.togedo.document.UserDocument
import vp.togedo.enums.OauthEnum
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [UserServiceImpl::class])
class UserServiceImplTest{

    @SpyBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    private lateinit var userRepository: UserRepository

    private lateinit var userServiceImpl: UserServiceImpl

    @BeforeEach
    fun setUp() {
        userServiceImpl = UserServiceImpl(jwtTokenProvider, userRepository)
    }

    @Nested
    inner class GetUserInfoByOauth{

        @Test
        @DisplayName("카카오 Oauth로 일치하는 유저 조회 성공")
        fun getUserInfoByOauthKakaoReturnSuccess(){
            //given
            val oauthType = OauthEnum.KAKAO
            val kakaoId = 0L
            val user = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(kakaoId = kakaoId)
            )

            `when`(userRepository.findByOauth_KakaoId(kakaoId))
                .thenReturn(Mono.just(user))

            //when && then
            StepVerifier.create(userServiceImpl.getUserInfoByOauth(
                oauthEnum = oauthType,
                kakaoId = kakaoId,
            ))
                .expectNextMatches {
                    user.id == it.id && kakaoId == it.oauth.kakaoId
                }.verifyComplete()

            verify(userRepository, times(1))
                .findByOauth_KakaoId(kakaoId)
        }

        @Test
        @DisplayName("카카오 Oauth로 일치하는 유저 조회 실패")
        fun getUserInfoByOauthKakaoReturnEmpty(){
            //given
            val oauthType = OauthEnum.KAKAO
            val kakaoId = 0L
            `when`(userRepository.findByOauth_KakaoId(kakaoId))
                .thenReturn(Mono.empty())

            //when && then
            StepVerifier.create(userServiceImpl.getUserInfoByOauth(
                oauthEnum = oauthType,
                kakaoId = kakaoId,
            )).expectErrorMatches {
                it is UserException && it.message == ErrorCode.USER_NOT_FOUND_BY_OAUTH.message
            }.verify()

            verify(userRepository, times(1))
                .findByOauth_KakaoId(kakaoId)
        }

        @Test
        @DisplayName("구글 Oauth로 일치하는 유저 조회 성공")
        fun getUserInfoByOauthGoogleReturnSuccess(){
            //given
            val oauthType = OauthEnum.GOOGLE
            val googleId = UUID.randomUUID().toString()
            val user = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(googleId = googleId)
            )

            `when`(userRepository.findByOauth_GoogleId(googleId))
                .thenReturn(Mono.just(user))

            //when && then
            StepVerifier.create(userServiceImpl.getUserInfoByOauth(
                oauthEnum = oauthType,
                googleId = googleId,
            ))
                .expectNextMatches {
                    googleId == it.oauth.googleId &&
                    user.id == it.id
                }.verifyComplete()

            verify(userRepository, times(1))
                .findByOauth_GoogleId(googleId)
        }

        @Test
        @DisplayName("구글 Oauth로 일치하는 유저 조회 실패")
        fun getUserInfoByOauthGoogleReturnEmpty(){
            //given
            val oauthType = OauthEnum.GOOGLE
            val googleId = UUID.randomUUID().toString()


            `when`(userRepository.findByOauth_GoogleId(googleId))
                .thenReturn(Mono.empty())

            //when && then
            StepVerifier.create(userServiceImpl.getUserInfoByOauth(
                oauthEnum = oauthType,
                googleId = googleId,
            )).expectErrorMatches {
                it is UserException && it.message == ErrorCode.USER_NOT_FOUND_BY_OAUTH.message
            }.verify()

            verify(userRepository, times(1))
                .findByOauth_GoogleId(googleId)
        }
    }



    @Nested
    inner class CreateUser{

        @Test
        @DisplayName("카카오 Oauth를 사용하여 사용자를 생성")
        fun createUserByOauthByKakao(){
            //given
            val oauthType = OauthEnum.KAKAO
            val kakaoId = 0L
            val oauth = Oauth(
                kakaoId = kakaoId
            )

            `when`(userRepository.save(any<UserDocument>()))
                .thenReturn(
                    Mono.just(UserDocument(id = ObjectId.get(), oauth = oauth))
                )

            StepVerifier.create(userServiceImpl.createUser(
                oauthEnum = oauthType,
                kakaoId = kakaoId,
            )).expectNextMatches {
                kakaoId == it.oauth.kakaoId
            }.verifyComplete()

            verify(userRepository, times(1))
                .save(any<UserDocument>())

        }

        @Test
        @DisplayName("구글 Oauth를 사용하여 사용자를 생성")
        fun createUserByOauthByGoogle(){
            //given
            val oauthType = OauthEnum.GOOGLE
            val googleId = UUID.randomUUID().toString()
            val oauth = Oauth(
                googleId = googleId
            )

            `when`(userRepository.save(any<UserDocument>()))
                .thenReturn(
                    Mono.just(UserDocument(id = ObjectId.get(), oauth = oauth))
                )

            StepVerifier.create(userServiceImpl.createUser(
                oauthEnum = oauthType,
                googleId = googleId,
            )).expectNextMatches {
                it.oauth.googleId == oauth.googleId
            }.verifyComplete()

            verify(userRepository, times(1))
                .save(any<UserDocument>())

        }

        @Test
        @DisplayName("해당 이메일이 있는 상태로 구글 Oauth 계정 생성")
        fun createUserWhenEmailDuplicateByGoogleOauth(){
            //given
            val oauthType = OauthEnum.GOOGLE
            val googleId = UUID.randomUUID().toString()
            val oauth = Oauth(
                googleId = googleId
            )

            `when`(userRepository.save(any<UserDocument>()))
                .thenReturn(Mono.error(DuplicateKeyException("이메일 중복")))
                .thenReturn(Mono.just(UserDocument(id = ObjectId.get(), oauth = oauth)))

            `when`(userRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(UserDocument(id = ObjectId.get(), oauth = oauth)))

            StepVerifier.create(userServiceImpl.createUser(
                oauthEnum = oauthType,
                googleId = googleId,
                email = "test@test.com",
            )).expectNextMatches {
                googleId == it.oauth.googleId
            }.verifyComplete()

            verify(userRepository, times(2))
                .save(any<UserDocument>())

            verify(userRepository, times(1))
                .findByEmail(anyString())
        }

        @Test
        @DisplayName("해당 이메일이 있는 상태로 카카오 Oauth 계정 생성")
        fun createUserWhenEmailDuplicateByKakaoOauth(){
            //given
            val oauthType = OauthEnum.KAKAO
            val kakaoId = 0L
            val oauth = Oauth(
                kakaoId = kakaoId
            )

            `when`(userRepository.save(any<UserDocument>()))
                .thenReturn(Mono.error(DuplicateKeyException("이메일 중복")))
                .thenReturn(Mono.just(UserDocument(id = ObjectId.get(), oauth = oauth)))

            `when`(userRepository.findByEmail(anyString()))
                .thenReturn(Mono.just(UserDocument(id = ObjectId.get(), oauth = oauth)))

            StepVerifier.create(userServiceImpl.createUser(
                oauthEnum = oauthType,
                kakaoId = kakaoId,
                email = "test@test.com",
            )).expectNextMatches {
                kakaoId == it.oauth.kakaoId
            }.verifyComplete()

            verify(userRepository, times(2))
                .save(any<UserDocument>())

            verify(userRepository, times(1))
                .findByEmail(anyString())
        }
    }

    @Nested
    inner class GetUserIdByToken{

        @Test
        @DisplayName("Token을 사용하여 사용자 ID를 추출")
        fun getUserIdByTokenReturnUserId(){
            //given
            val userId = ObjectId.get()
            val refreshToken = jwtTokenProvider.getRefreshToken(userId.toHexString())

            //when
            val extractedUserId = userServiceImpl.getUserIdByToken(refreshToken)

            //then
            Assertions.assertEquals(userId, extractedUserId)
        }

        @Test
        @DisplayName("유효하지 않은 토큰으로 에러가 발생")
        fun getUserIdByInvalidTokenReturnException(){
            //given
            val invalidToken = UUID.randomUUID().toString()

            //when & then
            Assertions.assertThrows(MalformedJwtException::class.java) {
                userServiceImpl.getUserIdByToken(invalidToken)
            }
        }

        @Test
        @DisplayName("유효하지 않은 아이디로 에러가 발생")
        fun getUserInvalidIdByTokenReturnException(){
            //given
            val invalidToken = UUID.randomUUID().toString()
            val refreshToken = jwtTokenProvider.getRefreshToken(invalidToken)

            //when & then
            Assertions.assertThrows(IllegalArgumentException::class.java) {
                userServiceImpl.getUserIdByToken(refreshToken)
            }

        }
    }

    @Nested
    inner class FindUser{
        @Test
        @DisplayName("유요한 Id로 유저를 검색")
        fun findUserByValidIdReturnSuccess(){
            //given
            val id = ObjectId.get()
            val userDocument = UserDocument(id = id, Oauth(kakaoId = 0L))
            `when`(userRepository.findById(any<ObjectId>()))
                .thenReturn(Mono.just(userDocument))

            //when & then
            StepVerifier.create(userServiceImpl.findUser(id))
                .expectNext(userDocument)
                .verifyComplete()

            verify(userRepository, times(1)).findById(id)
        }

        @Test
        @DisplayName("유요하지 않은 Id로 유저를 검색")
        fun findUserByInvalidIdReturnException(){
            //given
            val id = ObjectId.get()
            `when`(userRepository.findById(any<ObjectId>()))
                .thenReturn(Mono.empty())

            //when & then
            StepVerifier.create(userServiceImpl.findUser(id))
                .expectErrorMatches {
                    it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND
                }
                .verify()

            verify(userRepository, times(1)).findById(id)
        }
    }
}
package vp.togedo.service.impl

import io.jsonwebtoken.MalformedJwtException
import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
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
                oauth = Oauth(oauthType = oauthType, kakaoId = kakaoId)
            )

            `when`(userRepository.findByOauth(any<Oauth>()))
                .thenReturn(Mono.just(user))

            //when && then
            StepVerifier.create(userServiceImpl.getUserInfoByOauth(
                oauthEnum = oauthType,
                kakaoId = kakaoId,
            ))
                .expectNextMatches {
                    user.id == it.id && oauthType == it.oauth.oauthType && kakaoId == it.oauth.kakaoId
                }.verifyComplete()

            Mockito.verify(userRepository, times(1))
                .findByOauth(any<Oauth>())
        }

        @Test
        @DisplayName("카카오 Oauth로 일치하는 유저 조회 실패")
        fun getUserInfoByOauthKakaoReturnEmpty(){
            //given
            val oauthType = OauthEnum.KAKAO
            val kakaoId = 0L
            `when`(userRepository.findByOauth(any<Oauth>()))
                .thenReturn(Mono.empty())

            //when && then
            StepVerifier.create(userServiceImpl.getUserInfoByOauth(
                oauthEnum = oauthType,
                kakaoId = kakaoId,
            )).expectErrorMatches {
                it is UserException && it.message == ErrorCode.USER_NOT_FOUND_BY_OAUTH.message
            }.verify()

            Mockito.verify(userRepository, times(1))
                .findByOauth(any<Oauth>())
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
                oauthType = oauthType,
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
                oauthType == it.oauth.oauthType && kakaoId == it.oauth.kakaoId
            }.verifyComplete()

            Mockito.verify(userRepository, times(1))
                .save(any<UserDocument>())

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
}
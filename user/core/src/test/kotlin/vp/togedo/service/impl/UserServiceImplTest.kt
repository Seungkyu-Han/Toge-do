package vp.togedo.service.impl

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.enums.OauthEnum
import vp.togedo.model.documents.user.Oauth
import vp.togedo.model.documents.user.UserDocument
import vp.togedo.repository.UserRepository
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException
import java.util.*


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [UserServiceImpl::class])
class UserServiceImplTest{

    @MockBean
    private lateinit var userRepository: UserRepository

    private lateinit var userService: UserServiceImpl

    @BeforeEach
    fun setUp() {
        userService = UserServiceImpl(userRepository)
    }

    @Nested
    inner class GetUserInfoByAuth{

        @Test
        @DisplayName("카카오 oauth로 사용자를 검색")
        fun findUserByExistKakaoOauthReturnSuccess(){
            //given
            val kakaoId = 1L
            val user = UserDocument(
                oauth = Oauth(
                    kakaoId = kakaoId
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString()
            )

            `when`(userRepository.findByOauth_KakaoId(kakaoId))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.getUserInfoByOauth(
                oauthEnum = OauthEnum.KAKAO,
                kakaoId = kakaoId
            )).expectNext(user).verifyComplete()

            //then
            verify(userRepository, times(1)).findByOauth_KakaoId(kakaoId)
        }

        @Test
        @DisplayName("google oauth로 사용자를 검색")
        fun findUserByExistGoogleOauthReturnSuccess(){
            //given
            val googleId = UUID.randomUUID().toString()
            val user = UserDocument(
                oauth = Oauth(
                    googleId = googleId
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString()
            )

            `when`(userRepository.findByOauth_GoogleId(googleId))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.getUserInfoByOauth(
                oauthEnum = OauthEnum.GOOGLE,
                googleId = googleId
            )).expectNext(user).verifyComplete()

            //then
            verify(userRepository, times(1)).findByOauth_GoogleId(googleId)
        }

        @Test
        @DisplayName("kakao oauth로 존재하지 않는 사용자를 검색")
        fun findUserByNotExistKakaoOauthReturnSuccess(){
            //given
            val kakaoId = 1L

            `when`(userRepository.findByOauth_KakaoId(kakaoId))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(userService.getUserInfoByOauth(
                oauthEnum = OauthEnum.KAKAO,
                kakaoId = kakaoId
            )).expectErrorMatches {
                it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND_BY_OAUTH
            }.verify()

            //then
            verify(userRepository, times(1)).findByOauth_KakaoId(kakaoId)
        }

        @Test
        @DisplayName("google oauth로 존재하지 않는 사용자를 검색")
        fun findUserByNotExistGoogleOauthReturnSuccess(){
            //given
            val googleId = UUID.randomUUID().toString()

            `when`(userRepository.findByOauth_GoogleId(googleId))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(userService.getUserInfoByOauth(
                oauthEnum = OauthEnum.GOOGLE,
                googleId = googleId
            )).expectErrorMatches {
                it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND_BY_OAUTH
            }.verify()

            //then
            verify(userRepository, times(1)).findByOauth_GoogleId(googleId)
        }
    }

}
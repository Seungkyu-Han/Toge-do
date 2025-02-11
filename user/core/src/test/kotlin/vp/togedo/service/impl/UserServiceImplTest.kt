package vp.togedo.service.impl

import org.bson.types.ObjectId
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
        fun findUserByNotExistKakaoOauthReturnException(){
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
        fun findUserByNotExistGoogleOauthReturnException(){
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

    @Nested
    inner class CreateUser{

        @Test
        @DisplayName("존재하지 않는 유저를 kakao oauth를 통해 생성")
        fun createNewKakaoUserReturnSuccess(){
            //given
            val user = UserDocument(
                oauth = Oauth(
                    kakaoId = 1L
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                profileImageUrl = UUID.randomUUID().toString()
            )

            `when`(userRepository.findByEmail(user.email!!))
                .thenReturn(Mono.empty())

            `when`(userRepository.save(any()))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.createUser(
                oauthEnum = OauthEnum.KAKAO,
                kakaoId = user.oauth.kakaoId,
                email = user.email,
                googleId = null,
                name = user.name,
                profileImageUrl = user.profileImageUrl
            )).expectNext(user).verifyComplete()

            //then
            verify(userRepository, times(1)).findByEmail(user.email!!)
            verify(userRepository, times(1)).save(any())
        }

        @Test
        @DisplayName("존재하지 않는 유저를 google oauth를 통해 생성")
        fun createNewGoogleUserReturnSuccess(){
            //given
            val user = UserDocument(
                oauth = Oauth(
                    googleId = UUID.randomUUID().toString(),
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                profileImageUrl = UUID.randomUUID().toString()
            )

            `when`(userRepository.findByEmail(user.email!!))
                .thenReturn(Mono.empty())

            `when`(userRepository.save(any()))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.createUser(
                oauthEnum = OauthEnum.GOOGLE,
                googleId = user.oauth.googleId,
                email = user.email,
                name = user.name,
                profileImageUrl = user.profileImageUrl
            )).expectNext(user).verifyComplete()

            //then
            verify(userRepository, times(1)).findByEmail(user.email!!)
            verify(userRepository, times(1)).save(any())
        }

        @Test
        @DisplayName("존재하는 유저를 kakao oauth를 통해 생성")
        fun createExistKakaoUserReturnSuccess(){
            //given
            val user = UserDocument(
                oauth = Oauth(
                    googleId = UUID.randomUUID().toString(),
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                profileImageUrl = UUID.randomUUID().toString()
            )
            val kakaoId = 1L

            `when`(userRepository.findByEmail(user.email!!))
                .thenReturn(Mono.just(user))

            `when`(userRepository.save(user))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.createUser(
                oauthEnum = OauthEnum.KAKAO,
                kakaoId = kakaoId,
                email = user.email,
                name = user.name,
                profileImageUrl = user.profileImageUrl
            )).expectNextMatches{
                it.oauth.kakaoId == kakaoId &&
                        it.oauth.googleId == user.oauth.googleId
            }.verifyComplete()

            //then
            verify(userRepository, times(1)).findByEmail(user.email!!)
            verify(userRepository, times(1)).save(user)
        }


        @Test
        @DisplayName("존재하는 유저를 google oauth를 통해 생성")
        fun createExistGoogleUserReturnSuccess(){
            //given
            val user = UserDocument(
                oauth = Oauth(
                    kakaoId = 1L
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                profileImageUrl = UUID.randomUUID().toString()
            )
            val googleId = UUID.randomUUID().toString()

            `when`(userRepository.findByEmail(user.email!!))
                .thenReturn(Mono.just(user))

            `when`(userRepository.save(user))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.createUser(
                oauthEnum = OauthEnum.GOOGLE,
                googleId = googleId,
                email = user.email,
                name = user.name,
                profileImageUrl = user.profileImageUrl
            )).expectNextMatches{
                it.oauth.kakaoId == user.oauth.kakaoId &&
                        it.oauth.googleId == googleId
            }.verifyComplete()

            //then
            verify(userRepository, times(1)).findByEmail(user.email!!)
            verify(userRepository, times(1)).save(user)
        }
    }

    @Nested
    inner class FindUser{

        @Test
        @DisplayName("존재하는 유저를 검색")
        fun findExistUserReturnSuccess(){
            //given
            val user = UserDocument(
                oauth = Oauth(
                    kakaoId = 1L
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                profileImageUrl = UUID.randomUUID().toString()
            )

            `when`(userRepository.findById(user.id))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.findUser(user.id))
                .expectNext(user).verifyComplete()

            //then
            verify(userRepository, times(1)).findById(user.id)
        }

        @Test
        @DisplayName("존재하지 않는 유저를 검색")
        fun findNotExistUserReturnException(){
            //given
            val userId = ObjectId.get()

            `when`(userRepository.findById(userId))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(userService.findUser(userId))
                .expectErrorMatches {
                    it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND
                }.verify()

            //then
            verify(userRepository, times(1)).findById(userId)
        }
    }

    @Nested
    inner class FindUserByEmail{

        @Test
        @DisplayName("존재하는 유저를 검색")
        fun findExistUserByEmailReturnSuccess(){
            //given
            val user = UserDocument(
                oauth = Oauth(
                    kakaoId = 1L
                ),
                email = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                profileImageUrl = UUID.randomUUID().toString()
            )

            `when`(userRepository.findByEmail(user.email!!))
                .thenReturn(Mono.just(user))

            //when
            StepVerifier.create(userService.findUserByEmail(user.email!!))
                .expectNext(user).verifyComplete()

            //then
            verify(userRepository, times(1)).findByEmail(user.email!!)
        }

        @Test
        @DisplayName("존재하지 않는 유저를 검색")
        fun findNotExistUserByEmailReturnException(){
            //given
            val userEmail = UUID.randomUUID().toString()

            `when`(userRepository.findByEmail(userEmail))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(userService.findUserByEmail(userEmail))
                .expectErrorMatches {
                    it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND
                }.verify()

            //then
            verify(userRepository, times(1)).findByEmail(userEmail)
        }
    }

}
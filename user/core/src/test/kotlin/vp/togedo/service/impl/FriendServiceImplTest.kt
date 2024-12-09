package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.UserRepository
import vp.togedo.document.Oauth
import vp.togedo.document.UserDocument
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [FriendServiceImpl::class])
class FriendServiceImplTest{

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>

    private lateinit var friendService: FriendServiceImpl

    @BeforeEach
    fun setUp() {
        friendService = FriendServiceImpl(userRepository, reactiveKafkaProducerTemplate)
    }

    @Nested
    inner class RequestFriend{

        @Test
        @DisplayName("존재하는 사용자에게 정상적인 친구 요청")
        fun requestToValidUserReturnSuccess(){
            //given
            val userId = ObjectId.get()
            val friendId = ObjectId.get()
            val friendUserDocument = UserDocument(
                id = friendId,
                oauth = Oauth()
            )

            `when`(userRepository.findById(friendId))
                .thenReturn(Mono.just(friendUserDocument))

            `when`(userRepository.save(friendUserDocument))
                .thenReturn(Mono.just(friendUserDocument))

            //when
            StepVerifier.create(friendService.requestFriend(userId = userId, friendId = friendId))
                .expectNextMatches{
                    friendId == it.id && it.friendRequests.contains(userId)
                }
                .verifyComplete()

            //then
            verify(userRepository, times(1)).findById(friendId)
            verify(userRepository, times(1)).save(friendUserDocument)
        }


        @Test
        @DisplayName("존재하지 않는 사용자에게 친구 요청 후 에러")
        fun requestToInValidUserReturnException(){
            //given
            val userId = ObjectId.get()
            val friendId = ObjectId.get()

            `when`(userRepository.findById(friendId))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(friendService.requestFriend(userId = userId, friendId = friendId))
                .expectErrorMatches {
                    it is UserException && it.errorCode == ErrorCode.USER_NOT_FOUND
                }.verify()

            //then
            verify(userRepository, times(1)).findById(friendId)
        }

    }
}
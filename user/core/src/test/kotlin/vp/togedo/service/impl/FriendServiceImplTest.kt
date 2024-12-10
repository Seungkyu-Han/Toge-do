package vp.togedo.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.RecordMetadata
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import reactor.test.StepVerifier
import vp.togedo.UserRepository
import vp.togedo.data.dto.friend.FriendRequestEventDto
import vp.togedo.document.Oauth
import vp.togedo.document.UserDocument
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.FriendException
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [FriendServiceImpl::class])
class FriendServiceImplTest{

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>

    @SpyBean
    lateinit var objectMapper: ObjectMapper

    private lateinit var friendService: FriendServiceImpl

    @BeforeEach
    fun setUp() {
        friendService = FriendServiceImpl(
            userRepository = userRepository,
            objectMapper = objectMapper,
            reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate)
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
                name = UUID.randomUUID().toString(),
                oauth = Oauth()
            )

            `when`(userRepository.findById(friendId))
                .thenReturn(Mono.just(friendUserDocument))

            `when`(userRepository.save(friendUserDocument))
                .thenReturn(Mono.just(friendUserDocument))

            //when
            StepVerifier.create(friendService.requestFriend(userId = userId, friendUserDocument = friendUserDocument))
                .expectNextMatches{
                    friendId == it.id && it.friendRequests.contains(userId)
                }
                .verifyComplete()

            //then
            verify(userRepository, times(1)).save(friendUserDocument)
        }

        @Test
        @DisplayName("이미 친구인 사용자에게 친구 요청")
        fun requestToAlreadyFriendReturnException(){
            //given
            val userId = ObjectId.get()
            val friendId = ObjectId.get()
            val friendUserDocument = UserDocument(
                id = friendId,
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friends = mutableSetOf(userId),
            )

            `when`(userRepository.findById(friendId))
                .thenReturn(Mono.just(friendUserDocument))

            //when
            StepVerifier.create(friendService.requestFriend(userId = userId, friendUserDocument = friendUserDocument))
                .expectErrorMatches {
                    it is FriendException && it.errorCode == ErrorCode.ALREADY_FRIEND
                }
                .verify()
        }


        @Test
        @DisplayName("이미 친구 요청 보낸 사용자에게 친구 요청")
        fun requestToAlreadyFriendRequestedReturnException(){
            //given
            val userId = ObjectId.get()
            val friendId = ObjectId.get()
            val friendUserDocument = UserDocument(
                id = friendId,
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf(userId),
            )

            `when`(userRepository.findById(friendId))
                .thenReturn(Mono.just(friendUserDocument))

            //when
            StepVerifier.create(friendService.requestFriend(userId = userId, friendUserDocument = friendUserDocument))
                .expectErrorMatches {
                    it is FriendException && it.errorCode == ErrorCode.ALREADY_FRIEND_REQUESTED
                }
                .verify()
        }
    }

    @Nested
    inner class PublishRequestFriendEvent{

        private val friendRequestEventTopic = "FRIEND_REQUEST_TOPIC"

        @Test
        @DisplayName("정상적인 kafka 이벤트 전송")
        fun publishValidEventReturnSuccess(){
            //given
            val friendId = ObjectId.get()
            val publishMessage = objectMapper.writeValueAsString(FriendRequestEventDto(friendId = friendId.toString()))

            val recordMetadata = RecordMetadata(null, 0, 0, 0, 0, 0)

            // SenderResult 구현
            val senderResult = object : SenderResult<Void> {
                override fun recordMetadata(): RecordMetadata = recordMetadata
                override fun exception(): Exception? = null
                override fun correlationMetadata(): Void? = null
            }

            `when`(reactiveKafkaProducerTemplate.send(
                friendRequestEventTopic, publishMessage))
                .thenReturn(Mono.just(senderResult))

            //when

            StepVerifier.create(friendService.publishRequestFriendEvent(friendId))
                .expectNextCount(1)
                .verifyComplete()

            //then
            verify(reactiveKafkaProducerTemplate, times(1))
                .send(friendRequestEventTopic, publishMessage)
        }

    }

    @Nested
    inner class AcceptFriendRequest{

        @Test
        @DisplayName("친구 요청에 있는 사용자에게 친구 요청 승인")
        fun approveRequestToRequestedReturnSuccess(){
            //given
            val friendDocument = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf()
            )
            val userDocument = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf(friendDocument.id!!)
            )

            `when`(userRepository.findById(friendDocument.id!!))
                .thenReturn(Mono.just(friendDocument))

            `when`(userRepository.save(friendDocument))
                .thenReturn(Mono.just(friendDocument))

            `when`(userRepository.findById(userDocument.id!!))
                .thenReturn(Mono.just(userDocument))

            `when`(userRepository.save(userDocument))
                .thenReturn(Mono.just(userDocument))

            //when
            StepVerifier.create(friendService.acceptFriendRequest(
                userId = userDocument.id!!,
                friendId = friendDocument.id!!,
            )).expectNextMatches {
                it == friendDocument
            }.verifyComplete()

            //then
            verify(userRepository, times(1)).findById(friendDocument.id!!)
            verify(userRepository, times(1)).findById(userDocument.id!!)
            verify(userRepository, times(1)).save(friendDocument)
            verify(userRepository, times(1)).save(userDocument)

            Assertions.assertTrue(userDocument.friends.contains(friendDocument.id!!))
            Assertions.assertTrue(friendDocument.friends.contains(userDocument.id!!))
            Assertions.assertFalse(userDocument.friendRequests.contains(friendDocument.id!!))
        }

        @Test
        @DisplayName("친구 요청하지 않은 사용자에게 친구 요청 승인")
        fun approveRequestToNoRequestedReturnException(){
            //given
            val friendDocument = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf()
            )
            val userDocument = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf()
            )

            `when`(userRepository.findById(userDocument.id!!))
                .thenReturn(Mono.just(userDocument))
            //when
            StepVerifier.create(friendService.acceptFriendRequest(
                userId = userDocument.id!!,
                friendId = friendDocument.id!!,
            )).expectErrorMatches {
                it is FriendException && it.errorCode == ErrorCode.NO_REQUESTED
            }.verify()

            //then
            verify(userRepository, times(1)).findById(userDocument.id!!)

            Assertions.assertFalse(userDocument.friends.contains(friendDocument.id!!))
            Assertions.assertFalse(userDocument.friendRequests.contains(friendDocument.id!!))
        }

        @Test
        @DisplayName("이미 친구 상태인 사용자에게 친구 요청 승인")
        fun approveRequestToFriendReturnSuccess(){
            val userId = ObjectId.get()
            val friendId = ObjectId.get()
            //given
            val friendDocument = UserDocument(
                id = friendId,
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf(),
                friends = mutableSetOf(userId)
            )
            val userDocument = UserDocument(
                id = userId,
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf(),
                friends = mutableSetOf(friendId)
            )

            `when`(userRepository.findById(userDocument.id!!))
                .thenReturn(Mono.just(userDocument))

            //when
            StepVerifier.create(friendService.acceptFriendRequest(
                userId = userDocument.id!!,
                friendId = friendDocument.id!!,
            )).expectErrorMatches {
                it is FriendException && it.errorCode == ErrorCode.ALREADY_FRIEND
            }.verify()

            //then

            //then
            verify(userRepository, times(1)).findById(userDocument.id!!)
        }
    }

    @Nested
    inner class RemoveFriend{
        @Test
        @DisplayName("친구 목록에 존재하는 사용자를 삭제")
        fun removeFriendToExistFriendReturnSuccess(){
            //given
            val friendId = ObjectId.get()
            val userDocument = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf(),
                friends = mutableSetOf(friendId)
            )

            `when`(userRepository.findById(userDocument.id!!))
                .thenReturn(Mono.just(userDocument))

            `when`(userRepository.save(userDocument))
            .thenReturn(Mono.just(userDocument))

            //when
            StepVerifier.create(friendService.removeFriend(
                userId = userDocument.id!!,
                friendId = friendId))
                .expectNextMatches {
                    it == userDocument
                }.verifyComplete()

            //then
            Assertions.assertFalse(userDocument.friends.contains(friendId))

            verify(userRepository, times(1)).findById(userDocument.id!!)
            verify(userRepository, times(1)).save(userDocument)
        }

        @Test
        @DisplayName("친구 목록에 존재하지 않는 사용자를 삭제")
        fun removeFriendToNotExistFriendReturnSuccess(){
            //given
            val friendId = ObjectId.get()
            val userDocument = UserDocument(
                id = ObjectId.get(),
                oauth = Oauth(),
                name = UUID.randomUUID().toString(),
                friendRequests = mutableSetOf(),
                friends = mutableSetOf()
            )

            `when`(userRepository.findById(userDocument.id!!))
                .thenReturn(Mono.just(userDocument))

            //when
            StepVerifier.create(friendService.removeFriend(
                userId = userDocument.id!!,
                friendId = friendId))
                .expectErrorMatches {
                    it is FriendException && it.errorCode == ErrorCode.NOT_FRIEND
                }.verify()

            //then
            Assertions.assertFalse(userDocument.friends.contains(friendId))

            verify(userRepository, times(1)).findById(userDocument.id!!)
        }
    }
}
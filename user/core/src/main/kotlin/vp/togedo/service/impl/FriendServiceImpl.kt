package vp.togedo.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.UserRepository
import vp.togedo.data.dto.friend.FriendRequestEventDto
import vp.togedo.document.UserDocument
import vp.togedo.service.FriendService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.FriendException
import vp.togedo.util.error.exception.UserException
import vp.togedo.util.exception.AlreadyFriendException
import vp.togedo.util.exception.AlreadyFriendRequestException

@Service
class FriendServiceImpl(
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>
): FriendService {

    private val friendRequestEventTopic = "FRIEND_REQUEST_TOPIC"

    /**
     * 친구 목록에 있는 ID를 이용해 사용자를 검색해오는 메서드
     * @param friends 친구의 id 리스트
     * @return 유저 정보 Flux
     */
    override fun getUsersBySet(friends: Set<ObjectId>): Flux<UserDocument> {
        return userRepository.findAllById(friends)
    }

    /**
     * 해당 사용자에게 친구 요청을 보내는 메서드
     * @param userId 요청을 보내는 사용자의 id
     * @param friendUserDocument 친구 요청을 받는 사용자의 user document
     * @return 친구 요청을 받은 사용자의 user document
     * @throws UserException 이미 친구인 사용자
     */
    override fun requestFriend(userId: ObjectId, friendUserDocument: UserDocument): Mono<UserDocument> {
        try{
            friendUserDocument.requestFriend(userId)
        }catch(e: AlreadyFriendException){
            return Mono.error(FriendException(
                errorCode = ErrorCode.ALREADY_FRIEND, state = 0
            ))
        }catch (e: AlreadyFriendRequestException){
            return Mono.error(FriendException(ErrorCode.ALREADY_FRIEND_REQUESTED, state = 1))
        }
        return userRepository.save(friendUserDocument)
    }

    /**
     * 해당 사용자 아이디로 kafka 이벤트를 전송하는 메서드
     * @param friendId 친구 요청을 받는 사용자의 id
     * @return kafka send result
     */
    override fun publishRequestFriendEvent(friendId: ObjectId): Mono<SenderResult<Void>> =
        reactiveKafkaProducerTemplate.send(
            friendRequestEventTopic, objectMapper.writeValueAsString(FriendRequestEventDto(friendId = friendId.toString()))
        )

    /**
     * 해당 사용자와 친구 요청을 보낸 사용자의 친구 목록에 각각 친구를 저장하는 메서드
     * @param userId 친구 요청을 받은 사용자의 아이디
     * @param friendId 친구 요청을 보낸 사용자의 아이디
     * @return 친구 요청을 보낸 사용자의 user document
     * @throws UserException
     */
    override fun acceptFriendRequest(userId: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return userRepository.findById(userId)
            .map{
                if (!it.friendRequests.contains(friendId))
                    return@map Mono.error(UserException(ErrorCode.ALREADY_FRIEND))
                it.friendRequests.remove(friendId)
                it.friends.add(friendId)
                userRepository.save(it)
            }.then(userRepository.findById(friendId))
            .flatMap{
                it.friends.add(userId)
                userRepository.save(it)
            }
    }
}
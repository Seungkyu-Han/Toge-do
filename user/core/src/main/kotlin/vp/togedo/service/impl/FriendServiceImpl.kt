package vp.togedo.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.UserRepository
import vp.togedo.data.dto.friend.FriendApproveEventDto
import vp.togedo.data.dto.friend.FriendRequestEventDto
import vp.togedo.document.UserDocument
import vp.togedo.service.FriendService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.FriendException
import vp.togedo.util.error.exception.UserException
import vp.togedo.util.exception.*

@Service
class FriendServiceImpl(
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>
): FriendService {

    private val friendRequestEventTopic = "FRIEND_REQUEST_TOPIC"
    private val friendApproveEventTopic = "FRIEND_APPROVE_TOPIC"

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
        }catch (e: CantRequestToMeException){
            return Mono.error(FriendException(ErrorCode.CANT_REQUEST_TO_ME))
        }
        return userRepository.save(friendUserDocument)
    }

    /**
     * 해당 사용자 아이디로 kafka 이벤트를 전송하는 메서드
     * @param receiver 친구 요청을 받는 사용자의 user document
     * @param sender 친구 요청을 보낸 사용자의 user document
     * @return kafka send result
     */
    override fun publishRequestFriendEvent(receiver: UserDocument, sender: UserDocument): Mono<SenderResult<Void>> =
        reactiveKafkaProducerTemplate.send(
            friendRequestEventTopic, objectMapper.writeValueAsString(
                FriendRequestEventDto(
                    receiverId = receiver.id.toString(),
                    sender = sender.name,
                    deviceToken = receiver.deviceToken,
                    image = sender.profileImageUrl
                ))
        )

    /**
     * 해당 유저와 친구 관계를 삭제하는 메서드
     * @param userId 친구 삭제를 요청하는 유저
     * @param friendId 친구 삭제를 당하는 유저
     * @return 삭제한 유저의 user document
     */
    override fun removeFriend(userId: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return userRepository.findById(userId)
            .flatMap {
                it.removeFriend(friendId)
            }
            .flatMap{
                userRepository.save(it)
            }.onErrorMap{
                when(it){
                    is CantRequestToMeException -> FriendException(ErrorCode.CANT_REQUEST_TO_ME)
                    is NotFriendException -> FriendException(ErrorCode.NOT_FRIEND)
                    else -> it
                }
            }
    }

    /**
     * 해당 사용자 아이디로 kafka 이벤트를 전송하는 메서드
     * @param receiver 친구 요청을 보낸 사용자의 user document
     * @param sender 친구 요청을 받은 사용자의 user document
     * @return kafka send result
     */
    override fun publishApproveFriendEvent(receiver: UserDocument, sender: UserDocument): Mono<SenderResult<Void>> {
        return reactiveKafkaProducerTemplate.send(
            friendApproveEventTopic, objectMapper.writeValueAsString(
                FriendApproveEventDto(
                    receiverId = receiver.id.toString(),
                    sender = sender.name,
                    deviceToken = receiver.deviceToken,
                    image = sender.profileImageUrl
                    )))
    }

    /**
     * 친구 요청을 받은 사용자가 보낸 사용자의 요청을 수락하는 메서드
     * @param receiverId 요청을 받은 사용자의 id
     * @param senderId 요청을 보낸 사용자의 id
     * @return 요청을 받은 사용자의 user document
     * @throws FriendException 사용자와 이미 친구 관계이거나 사용자에게 친구 요청이 오지 않았던 경우
     */
    override fun approveFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument> {
        return userRepository.findById(receiverId)
            .flatMap {
                it.approveFriend(senderId)
            }
            .flatMap {
                userRepository.save(it)
            }.onErrorMap {
                when(it){
                    is CantRequestToMeException ->  FriendException(ErrorCode.CANT_REQUEST_TO_ME)
                    is AlreadyFriendException ->  FriendException(ErrorCode.ALREADY_FRIEND)
                    is NoFriendRequestException -> FriendException(ErrorCode.NO_REQUESTED)
                    else -> it
                }
            }
    }

    override fun rejectRequest(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument> {
        return userRepository.findById(receiverId)
            .flatMap {
                it.removeRequest(senderId)
            }
            .flatMap {
                userRepository.save(it)
            }
            .onErrorMap {
                when(it){
                    is CantRequestToMeException ->  FriendException(ErrorCode.CANT_REQUEST_TO_ME)
                    is NoFriendRequestException ->  FriendException(ErrorCode.NO_REQUESTED)
                    else -> it
                }
            }
    }

    /**
     * 친구 요청을 보냈던 사용자에게 친구를 추가해주는 메서드
     * @param receiverId 친구 요청을 보냈던 사용자의 id
     * @param senderId 수락한 사용자의 id
     * @return 요청을 보냈던 사용자의 user document
     * @throws FriendException 사용자와 이미 친구 관계인 경우
     */
    override fun addFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument> {
        return userRepository.findById(receiverId)
            .flatMap {
                it.addFriend(senderId)
            }
            .flatMap {
                userRepository.save(it)
            }.onErrorMap {
                if (it is AlreadyFriendException)
                    throw FriendException(ErrorCode.ALREADY_FRIEND)
                it
            }
    }
}
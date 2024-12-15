package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.document.UserDocument
import vp.togedo.util.error.exception.FriendException
import vp.togedo.util.error.exception.UserException

interface FriendService {

    fun getUsersBySet(friends: Set<ObjectId>): Flux<UserDocument>

    /**
     * 해당 사용자에게 친구 요청을 보내는 메서드
     * @param userId 요청을 보내는 사용자의 id
     * @param friendUserDocument 친구 요청을 받는 사용자의 user document
     * @return 친구 요청을 받은 사용자의 user document
     * @throws UserException 이미 친구인 사용자
     */
    fun requestFriend(userId: ObjectId, friendUserDocument: UserDocument): Mono<UserDocument>

    /**
     * 친구 요청을 받은 사용자가 보낸 사용자의 요청을 수락하는 메서드
     * @param receiverId 요청을 받은 사용자의 id
     * @param senderId 요청을 보낸 사용자의 id
     * @return 요청을 받은 사용자의 user document
     * @throws FriendException 사용자와 이미 친구 관계이거나 사용자에게 친구 요청이 오지 않았던 경우
     */
    fun approveFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    fun addFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    /**
     * 해당 사용자의 친구 요청을 거절하는 메서드
     * @param receiverId 친구 요청을 받았던 사용자 id
     * @param senderId 친구 요청을 보냈던 사용자 id
     * @return 요청을 받았던 사용자의 user document
     * @throws FriendException 해당 사용자의 요청이 오지 않았던 경우
     */
    fun rejectRequest(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    fun removeFriend(userId: ObjectId, friendId: ObjectId): Mono<UserDocument>

    fun publishRequestFriendEvent(receiver: UserDocument, sender: UserDocument): Mono<SenderResult<Void>>

    fun publishApproveFriendEvent(receiver: UserDocument, sender: UserDocument): Mono<SenderResult<Void>>
}
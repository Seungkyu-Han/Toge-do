package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.document.UserDocument
import vp.togedo.util.error.exception.FriendException

interface FriendService {

    fun getUsersBySet(friends: Set<ObjectId>): Flux<UserDocument>

    fun requestFriend(userId: ObjectId, friendUserDocument: UserDocument): Mono<UserDocument>

    fun acceptFriendRequest(userId: ObjectId, friendId: ObjectId): Mono<UserDocument>

    /**
     * 친구 요청을 받은 사용자가 보낸 사용자의 요청을 수락하는 메서드
     * @param receiverId 요청을 받은 사용자의 id
     * @param senderId 요청을 보낸 사용자의 id
     * @return 요청을 받은 사용자의 user document
     * @throws FriendException 사용자와 이미 친구 관계이거나 사용자에게 친구 요청이 오지 않았던 경우
     */
    fun approveFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    fun addFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    fun removeFriend(userId: ObjectId, friendId: ObjectId): Mono<UserDocument>

    fun publishRequestFriendEvent(receiverId: ObjectId, sender: String): Mono<SenderResult<Void>>

    fun publishApproveFriendEvent(receiverId: ObjectId, sender: String): Mono<SenderResult<Void>>
}
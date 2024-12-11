package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.document.UserDocument

interface FriendService {

    fun getUsersBySet(friends: Set<ObjectId>): Flux<UserDocument>

    fun requestFriend(userId: ObjectId, friendUserDocument: UserDocument): Mono<UserDocument>

    fun acceptFriendRequest(userId: ObjectId, friendId: ObjectId): Mono<UserDocument>

    fun approveFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    fun addFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    fun removeFriend(userId: ObjectId, friendId: ObjectId): Mono<UserDocument>

    fun publishRequestFriendEvent(receiverId: ObjectId, sender: String): Mono<SenderResult<Void>>

    fun publishApproveFriendEvent(receiverId: ObjectId, sender: String): Mono<SenderResult<Void>>
}
package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.document.UserDocument

interface FriendService {

    fun getUserByFriends(friends: Set<ObjectId>): Flux<UserDocument>

    fun requestFriend(userId: ObjectId, friendId: ObjectId): Mono<UserDocument>

    fun publishRequestFriendEvent(friendId: ObjectId): Mono<SenderResult<Void>>
}
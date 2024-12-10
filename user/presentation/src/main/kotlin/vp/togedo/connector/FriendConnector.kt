package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.document.UserDocument

interface FriendConnector {

    fun getFriendsInfo(id: ObjectId): Flux<UserDocument>

    fun getFriendRequests(id: ObjectId): Flux<UserDocument>

    fun requestFriendById(id: ObjectId, friendId: ObjectId): Mono<UserDocument>

    fun requestFriendByEmail(id: ObjectId, email: String): Mono<UserDocument>

    fun approveFriend(id: ObjectId, friendId: ObjectId): Mono<UserDocument>
}
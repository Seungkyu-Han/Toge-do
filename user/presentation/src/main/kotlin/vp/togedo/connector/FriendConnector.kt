package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.document.UserDocument

interface FriendConnector {

    fun getFriendsInfo(id: ObjectId): Flux<UserDocument>

    fun getFriendRequests(id: ObjectId): Flux<UserDocument>

    suspend fun requestFriendById(id: ObjectId, friendId: ObjectId): UserDocument

    suspend fun requestFriendByEmail(id: ObjectId, email: String): UserDocument

    suspend fun approveFriend(id: ObjectId, friendId: ObjectId): UserDocument

    fun rejectFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument>

    fun disconnectFriend(id: ObjectId, friendId: ObjectId): Mono<UserDocument>
}
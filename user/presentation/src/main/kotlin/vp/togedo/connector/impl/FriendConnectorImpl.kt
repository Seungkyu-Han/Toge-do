package vp.togedo.connector.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.connector.FriendConnector
import vp.togedo.document.UserDocument
import vp.togedo.service.FriendService
import vp.togedo.service.UserService

@Service
class FriendConnectorImpl(
    private val userService: UserService,
    private val friendService: FriendService
): FriendConnector {

    override fun getFriendsInfo(id: ObjectId): Flux<UserDocument> {
        return userService.findUser(id)
            .flatMapMany { friendService.getUsersBySet(it.friends)}
    }

    override fun getFriendRequests(id: ObjectId): Flux<UserDocument> {
        return userService.findUser(id)
            .flatMapMany { friendService.getUsersBySet(it.friendRequests) }
    }

    override suspend fun requestFriendById(id: ObjectId, friendId: ObjectId): UserDocument {

        val receiverDocument = userService.findUser(friendId).awaitSingle()

        friendService.requestFriend(id, receiverDocument).awaitSingle()

        CoroutineScope(Dispatchers.IO).launch {
            val senderDocument = userService.findUser(id).awaitSingle()
            friendService.publishRequestFriendEvent(
                receiver = receiverDocument,
                sender = senderDocument.name,
            ).awaitSingleOrNull()
        }

        return receiverDocument
    }

    override suspend fun requestFriendByEmail(id: ObjectId, email: String): UserDocument {

        val receiverDocument = userService.findUserByEmail(email).awaitSingle()

        friendService.requestFriend(id, receiverDocument).awaitSingle()

        CoroutineScope(Dispatchers.IO).launch {
            val senderDocument = userService.findUser(id).awaitSingle()
            friendService.publishRequestFriendEvent(
                receiver = receiverDocument,
                sender = senderDocument.name
            ).awaitSingleOrNull()
        }

        return receiverDocument
    }

    override suspend fun approveFriend(id: ObjectId, friendId: ObjectId): UserDocument {

        val receiverDocument = friendService.approveFriend(id, friendId).awaitSingle()

        val senderDocument = friendService.addFriend(friendId, id).awaitSingle()

        CoroutineScope(Dispatchers.IO).launch {
            friendService.publishApproveFriendEvent(
                receiver = senderDocument,
                sender = receiverDocument.name,
            ).awaitSingleOrNull()
        }

        return receiverDocument
    }

    override fun rejectFriend(receiverId: ObjectId, senderId: ObjectId): Mono<UserDocument> {
        return friendService.rejectRequest(receiverId, senderId)
    }

    override fun disconnectFriend(id: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return friendService.removeFriend(id, friendId)
            .flatMap {
                friendService.removeFriend(friendId, id)
            }
    }
}
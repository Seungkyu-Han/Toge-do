package vp.togedo.connector.impl

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
            .flatMapMany { friendService.getUserByFriends(it.friends)}
    }

    override fun requestFriendById(id: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return userService.findUser(friendId)
            .flatMap {
                friendService.requestFriend(id, it)
            }
    }

    override fun requestFriendByEmail(id: ObjectId, email: String): Mono<UserDocument> {
        return userService.findUserByEmail(email)
            .flatMap {
                friendService.requestFriend(id, it)
            }
    }
}
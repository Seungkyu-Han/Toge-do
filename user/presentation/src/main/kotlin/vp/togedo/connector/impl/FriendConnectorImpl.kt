package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
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

    override fun requestFriendById(id: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return userService.findUser(friendId)
            .flatMap {
                friendDocument ->
                friendService.requestFriend(id, friendDocument)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess {
                        userService.findUser(id)
                            .map{
                                userDocument ->
                                friendService.publishRequestFriendEvent(friendDocument.id!!, userDocument.name)
                            }.block()
                    }
            }
    }

    override fun requestFriendByEmail(id: ObjectId, email: String): Mono<UserDocument> {
        return userService.findUserByEmail(email)
            .flatMap {
                    friendDocument ->
                friendService.requestFriend(id, friendDocument)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess {
                        userService.findUser(id)
                            .map{
                                userDocument ->
                                friendService.publishRequestFriendEvent(friendDocument.id!!, userDocument.name)
                            }.block()
                    }
            }
    }

    override fun approveFriend(id: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return friendService.acceptFriendRequest(id, friendId)
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                receiver ->
                userService.findUser(id)
                    .flatMap{
                        userDocument ->
                        friendService.publishApproveFriendEvent(receiverId = receiver.id!!, userDocument.name)
                    }.block()

            }
    }

    override fun disconnectFriend(id: ObjectId, friendId: ObjectId): Mono<UserDocument> {
        return friendService.removeFriend(id, friendId)
            .flatMap {
                friendService.removeFriend(friendId, id)
            }
    }
}
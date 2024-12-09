package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import vp.togedo.document.UserDocument

interface FriendService {

    fun getUserByFriends(friends: List<ObjectId>): Flux<UserDocument>
}
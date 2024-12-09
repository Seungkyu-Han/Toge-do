package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import vp.togedo.document.UserDocument

interface FriendConnector {

    fun getFriendsInfo(id: ObjectId): Flux<UserDocument>
}
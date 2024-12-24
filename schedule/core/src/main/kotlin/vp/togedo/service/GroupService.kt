package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.document.GroupDocument
import vp.togedo.document.JoinedGroupDocument

interface GroupService {

    fun createGroup(name: String, members: List<ObjectId>): Mono<GroupDocument>

    fun addUserToGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDocument>

    fun addGroupToJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDocument>
}
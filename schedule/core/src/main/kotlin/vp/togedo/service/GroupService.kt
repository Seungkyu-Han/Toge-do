package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dao.GroupDao
import vp.togedo.document.GroupDocument
import vp.togedo.document.JoinedGroupDocument

interface GroupService {

    fun createGroup(name: String, members: List<ObjectId>): Mono<GroupDocument>

    fun addUserToGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDocument>

    fun removeUserFromGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDocument>

    fun addGroupToJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDocument>

    fun removeGroupFromJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDocument>

    fun updateGroup(groupId: ObjectId, groupDao: GroupDao): Mono<GroupDocument>

    fun readGroups(userId: ObjectId): Flux<GroupDao>
}
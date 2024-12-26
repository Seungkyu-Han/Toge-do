package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dao.GroupDao
import vp.togedo.data.dao.JoinedGroupDao

interface GroupService {

    fun createGroup(name: String, members: List<ObjectId>): Mono<GroupDao>

    fun addUserToGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDao>

    fun removeUserFromGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDao>

    fun addGroupToJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDao>

    fun removeGroupFromJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDao>

    fun updateGroup(groupDao: GroupDao): Mono<GroupDao>

    fun readGroups(userId: ObjectId): Flux<GroupDao>

    fun readGroup(groupId: ObjectId): Mono<GroupDao>
}
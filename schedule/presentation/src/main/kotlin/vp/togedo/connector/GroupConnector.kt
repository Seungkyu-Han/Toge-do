package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dto.group.CreateGroupReqDto
import vp.togedo.data.dto.group.GroupDto
import vp.togedo.data.dto.group.UpdateGroupReqDto

interface GroupConnector {

    fun createGroup(userId: ObjectId, createGroupReqDto: CreateGroupReqDto): Mono<Void>

    fun readGroups(userId: ObjectId): Flux<GroupDto>

    fun readGroup(groupId: ObjectId): Mono<GroupDto>

    fun addUserToGroup(addedId: String, groupId: String): Mono<Void>

    fun exitGroup(userId: ObjectId, groupId: String): Mono<Void>

    fun modifyGroup(updateGroupReqDto: UpdateGroupReqDto): Mono<Void>
}
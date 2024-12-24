package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.data.dto.group.CreateGroupReqDto

interface GroupConnector {

    fun createGroup(userId: ObjectId, createGroupReqDto: CreateGroupReqDto): Mono<Void>

    fun addUserToGroup(addedId: String, groupId: String): Mono<Void>
}
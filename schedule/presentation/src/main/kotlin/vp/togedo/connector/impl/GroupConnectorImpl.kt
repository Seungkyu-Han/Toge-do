package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.connector.GroupConnector
import vp.togedo.data.dto.group.CreateGroupReqDto
import vp.togedo.data.dto.group.GroupDto
import vp.togedo.service.GroupService
import vp.togedo.service.KafkaService

@Service
class GroupConnectorImpl(
    private val groupService: GroupService,
    private val kafkaService: KafkaService
): GroupConnector {

    override fun createGroup(userId: ObjectId, createGroupReqDto: CreateGroupReqDto): Mono<Void> {
        val userIdList = createGroupReqDto.members.map{
            ObjectId(it)
        }

        return groupService.createGroup(
            name = createGroupReqDto.name,
            members = userIdList + userId
        ).map{
            group ->
            group.members.forEach{
                userId ->
                groupService.addGroupToJoinedGroup(
                    userId = userId,
                    groupId = group.id,
                ).publishOn(Schedulers.boundedElastic())
                    .doOnSuccess {
                        kafkaService.publishInviteGroupEvent(
                            receiverId = userId,
                            group = group).block()
                    }.block()
            }
        }.then()
    }

    override fun readGroups(userId: ObjectId): Flux<GroupDto> =
        groupService.readGroups(userId)
            .map{
                groupDao ->
                GroupDto(
                    id = groupDao.id.toString(),
                    name = groupDao.name,
                    members = groupDao.members.map{it.toString()}
                )
            }


    @Transactional
    override fun addUserToGroup(addedId: String, groupId: String): Mono<Void> {
        val userObjectId = ObjectId(addedId)
        val groupObjectId = ObjectId(groupId)
        return groupService.addUserToGroup(
            userId = userObjectId,
            groupId = groupObjectId
        ).map{
            groupService.addGroupToJoinedGroup(
                userId = userObjectId,
                groupId = groupObjectId
            )
        }.then()
    }

    @Transactional
    override fun exitGroup(userId: ObjectId, groupId: String): Mono<Void> {
        val groupObjectId = ObjectId(groupId)

        return groupService.removeUserFromGroup(
            userId = userId,
            groupId = groupObjectId
        ).map{
            groupService.removeGroupFromJoinedGroup(
                userId = userId,
                groupId = groupObjectId
            )
        }.then()
    }
}
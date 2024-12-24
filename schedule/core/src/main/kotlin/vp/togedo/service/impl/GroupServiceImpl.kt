package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.data.dao.GroupDao
import vp.togedo.document.GroupDocument
import vp.togedo.document.JoinedGroupDocument
import vp.togedo.repository.GroupRepository
import vp.togedo.repository.JoinedGroupRepository
import vp.togedo.service.GroupService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.GroupException
import vp.togedo.util.exception.group.AlreadyJoinedGroupException

@Service
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val joinedGroupRepository: JoinedGroupRepository
): GroupService {

    override fun createGroup(name: String, members: List<ObjectId>): Mono<GroupDocument> {
        val group = GroupDocument(
            name = name,
            members = members.toMutableSet()
        )
        return groupRepository.save(group)
    }

    override fun addUserToGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDocument> {
        return groupRepository.findById(groupId)
            .flatMap {
                it.addMember(userId)
            }.flatMap {
                groupRepository.save(it)
            }
    }

    override fun removeUserFromGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDocument> {
        return groupRepository.findById(groupId)
            .flatMap {
                it.removeMember(userId)
            }
            .flatMap {
                groupRepository.save(it)
            }
    }

    override fun addGroupToJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDocument> {
        return joinedGroupRepository.findById(userId)
            .flatMap{
                it.addGroup(groupId)
            }.flatMap{
                joinedGroupRepository.save(it)
            }
            .onErrorMap{
                when(it){
                    is AlreadyJoinedGroupException -> GroupException(ErrorCode.ALREADY_JOINED_GROUP)
                    else -> it
                }
            }
    }

    override fun removeGroupFromJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDocument> {
        return joinedGroupRepository.findById(userId)
            .flatMap {
                it.removeGroup(groupId)
            }
            .flatMap {
                joinedGroupRepository.save(it)
            }
    }

    override fun updateGroup(groupId: ObjectId, groupDao: GroupDao): Mono<GroupDocument> {
        return groupRepository.findById(groupId)
            .flatMap{
                it.changeName(groupDao.name)
            }
            .flatMap {
                groupRepository.save(it)
            }
    }
}
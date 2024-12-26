package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.data.dao.GroupDao
import vp.togedo.data.dao.JoinedGroupDao
import vp.togedo.document.GroupDocument
import vp.togedo.document.JoinedGroupDocument
import vp.togedo.repository.GroupRepository
import vp.togedo.repository.JoinedGroupRepository
import vp.togedo.service.GroupService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.GroupException
import vp.togedo.util.error.exception.ScheduleException
import vp.togedo.util.exception.group.AlreadyJoinedGroupException
import vp.togedo.util.exception.group.NotJoinedGroupException

@Service
class GroupServiceImpl(
    private val groupRepository: GroupRepository,
    private val joinedGroupRepository: JoinedGroupRepository
): GroupService {

    override fun createGroup(name: String, members: List<ObjectId>): Mono<GroupDao> {

        val memberSet = members.toMutableSet()

        if (memberSet.size < 2)
            return Mono.error(ScheduleException(ErrorCode.REQUIRE_MORE_MEMBER))

        val group = GroupDocument(
            name = name,
            members = memberSet
        )
        return groupRepository.save(group).map{
            GroupDao(
                id = it.id,
                name = it.name,
                members = it.members.toList()
            )
        }
    }

    override fun addUserToGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                it.addMember(userId)
            }.flatMap {
                groupRepository.save(it)
            }.map{
            GroupDao(
                id = it.id,
                name = it.name,
                members = it.members.toList()
            )
        }.onErrorMap {
                when (it) {
                    is AlreadyJoinedGroupException -> GroupException(ErrorCode.ALREADY_JOINED_GROUP)
                    else -> it
                }
            }
    }

    override fun removeUserFromGroup(userId: ObjectId, groupId: ObjectId): Mono<GroupDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                group ->
                group.removeMember(userId)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext{
                        removedGroup ->
                        (if(removedGroup.members.isNotEmpty())
                            groupRepository.save(removedGroup)
                        else
                            groupRepository.delete(removedGroup)).subscribe()
                    }
            }
            .onErrorMap{
                when(it){
                    is NotJoinedGroupException -> GroupException(ErrorCode.NOT_JOINED_GROUP)
                    else -> it
                }
            }
            .map {
                GroupDao(
                    id = it.id,
                    name = it.name,
                    members = it.members.toList()
                )
            }
    }

        override fun addGroupToJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDao> {
        return joinedGroupRepository.findById(userId)
            .switchIfEmpty(
                Mono.defer{joinedGroupRepository.save(JoinedGroupDocument(id = userId))}
            )
            .flatMap{
                it.addGroup(groupId)
            }.flatMap{
                joinedGroupRepository.save(it)
            }.map{
                JoinedGroupDao(
                    id = it.id,
                    groups = it.groups
                )
            }
            .onErrorMap{
                when(it){
                    is AlreadyJoinedGroupException -> GroupException(ErrorCode.ALREADY_JOINED_GROUP)
                    else -> it
                }
            }
    }

    override fun removeGroupFromJoinedGroup(userId: ObjectId, groupId: ObjectId): Mono<JoinedGroupDao> {
        return joinedGroupRepository.findById(userId)
            .flatMap {
                it.removeGroup(groupId)
            }
            .flatMap {
                joinedGroupRepository.save(it)
            }.map{
                JoinedGroupDao(
                    id = it.id,
                    groups = it.groups
                )
            }
    }

    override fun updateGroup(groupDao: GroupDao): Mono<GroupDao> {
        return groupRepository.findById(groupDao.id)
            .flatMap{
                it.changeName(groupDao.name)
            }
            .flatMap {
                groupRepository.save(it)
            }.map{
                GroupDao(
                    id = it.id,
                    name = it.name,
                    members = it.members.toList()
                )
            }
    }

    override fun readGroups(userId: ObjectId): Flux<GroupDao> =
        joinedGroupRepository.findById(userId)
            .switchIfEmpty(
                Mono.defer{joinedGroupRepository.save(JoinedGroupDocument(id = userId))}
            )
            .flatMapMany { joinedGroup ->
                Flux.fromIterable(joinedGroup.groups)
                    .flatMap { groupId ->
                        groupRepository.findById(groupId)
                            .map { group ->
                                GroupDao(
                                    id = group.id,
                                    name = group.name,
                                    members = group.members.toList()
                                )
                            }
                    }
            }

}
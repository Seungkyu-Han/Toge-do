package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dao.groupSchedule.GroupScheduleDao
import vp.togedo.data.dao.groupSchedule.PersonalScheduleDao
import vp.togedo.data.dao.groupSchedule.PersonalSchedulesDao
import vp.togedo.document.GroupSchedule
import vp.togedo.repository.GroupRepository
import vp.togedo.service.GroupScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.GroupException
import vp.togedo.util.exception.group.CantCreateMoreScheduleException

@Service
class GroupScheduleServiceImpl(
    private val groupRepository: GroupRepository
): GroupScheduleService {

    override fun createGroupSchedule(groupId: ObjectId, name: String, startDate: Long, endDate: Long): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId).flatMap{
            group ->
            group.createGroupSchedule(
                name = name,
                startDate = startDate,
                endDate = endDate,
            )
        }.flatMap {
            groupRepository.save(it)
        }.map{
            group ->
            val createdGroupSchedule = group.groupSchedules.last()
            this.groupScheduleToDao(createdGroupSchedule)
        }.onErrorMap {
            when(it){
                is CantCreateMoreScheduleException -> GroupException(ErrorCode.CANT_CREATE_MORE_SCHEDULE)
                else -> it
            }
        }
    }

    override fun readGroupSchedules(groupId: ObjectId): Flux<GroupScheduleDao> =
        groupRepository.findById(groupId)
            .flatMapMany {
            group ->
            Flux.fromIterable(group.groupSchedules)
                .map{
                    groupSchedule ->
                    GroupScheduleDao(
                        id = groupSchedule.id,
                        name = groupSchedule.name,
                        startDate = groupSchedule.startDate,
                        endDate = groupSchedule.endDate,
                        personalScheduleMap = null
                    )
                }
        }


    private fun groupScheduleToDao(groupSchedule: GroupSchedule): GroupScheduleDao =
        GroupScheduleDao(
            id = groupSchedule.id,
            name = groupSchedule.name,
            startDate = groupSchedule.startDate,
            endDate = groupSchedule.endDate,
            personalScheduleMap = groupSchedule.personalScheduleMap.mapValues {
                PersonalSchedulesDao(
                    personalSchedules = it.value.personalSchedules.map{
                        personalScheduleInGroup ->
                        PersonalScheduleDao(
                            startTime = personalScheduleInGroup.startTime,
                            endTime = personalScheduleInGroup.endTime,
                        )
                    }
                )
            }
        )
}
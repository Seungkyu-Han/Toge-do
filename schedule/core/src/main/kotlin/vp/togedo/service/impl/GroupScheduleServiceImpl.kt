package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.data.dao.groupSchedule.*
import vp.togedo.document.GroupSchedule
import vp.togedo.document.PersonalSchedule
import vp.togedo.enums.GroupScheduleStateEnum
import vp.togedo.repository.GroupRepository
import vp.togedo.service.GroupScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.GroupException
import vp.togedo.util.error.exception.GroupScheduleException
import vp.togedo.util.exception.groupSchedule.CantCreateMoreScheduleException
import vp.togedo.util.exception.groupSchedule.NotFoundGroupScheduleException
import vp.togedo.util.exception.groupSchedule.NotFoundPersonalScheduleException

@Service
class GroupScheduleServiceImpl(
    private val groupRepository: GroupRepository
): GroupScheduleService {

    override fun createGroupSchedule(groupId: ObjectId, name: String, startDate: Long, endDate: Long, startTime: String, endTime: String): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId).flatMap{
            group ->
            group.createGroupSchedule(
                name = name,
                startDate = startDate,
                endDate = endDate,
                startTime = startTime,
                endTime = endTime
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
                        startTime = groupSchedule.startTime,
                        endTime = groupSchedule.endTime,
                        personalScheduleMap = null,
                        confirmScheduleDao = null
                    )
                }
        }

    override fun readGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                it.findGroupScheduleById(scheduleId)
            }
            .map {
                groupScheduleToDao(it)
            }
            .onErrorMap {
            when(it){
                is NotFoundGroupScheduleException -> GroupScheduleException(ErrorCode.GROUP_SCHEDULE_CANT_FIND)
                else -> it
            }
        }
    }

    override fun updateGroupSchedule(groupId: ObjectId, groupScheduleDao: GroupScheduleDao): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                group ->
                group.updateGroupSchedule(
                    scheduleId = groupScheduleDao.id!!,
                    name = groupScheduleDao.name,
                    startDate = groupScheduleDao.startDate,
                    endDate = groupScheduleDao.endDate
                ).publishOn(Schedulers.boundedElastic()).doOnSuccess {
                    groupRepository.save(group).subscribe()
                }
            }.map{
                groupScheduleToDao(it)
            }.onErrorMap {
                when(it){
                    is NotFoundGroupScheduleException -> GroupScheduleException(ErrorCode.GROUP_SCHEDULE_CANT_FIND)
                    else -> it
                }
            }
    }

    override fun deleteGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<Void> =
        groupRepository.findById(groupId)
            .flatMap {
                it.deleteGroupScheduleById(scheduleId)
            }.flatMap {
                groupRepository.save(it)
            }
            .onErrorMap {
                when(it){
                    is NotFoundGroupScheduleException -> GroupScheduleException(ErrorCode.GROUP_SCHEDULE_CANT_FIND)
                    else -> it
                }
            }
            .then()

    override fun addPersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        personalSchedulesDao: PersonalSchedulesDao
    ): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                group ->
                group.findGroupScheduleById(scheduleId)
                    .flatMap { groupSchedule ->
                        val userPersonalSchedule = groupSchedule.findGroupScheduleByUserId(userId)

                        val insertPersonalSchedules = personalSchedulesDaoToDocumentList(personalSchedulesDao)

                        userPersonalSchedule
                            .addPersonalSchedules(insertPersonalSchedules)
                            .then(groupRepository.save(group))
                            .then(Mono.just(groupSchedule))
                    }
            }.map(::groupScheduleToDao)
            .onErrorMap {
                when(it){
                    is GroupScheduleException -> GroupScheduleException(ErrorCode.USER_NOT_JOINED_SCHEDULE)
                    else -> it
                }
            }
    }

    override fun updatePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        personalSchedulesDao: PersonalSchedulesDao
    ): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                group ->
                group.findGroupScheduleById(scheduleId)
                    .flatMap {
                        groupSchedule ->
                        val userPersonalSchedule = groupSchedule.findGroupScheduleByUserId(userId)

                        val updatePersonalSchedules = personalSchedulesDaoToDocumentList(personalSchedulesDao)

                        userPersonalSchedule
                            .updatePersonalSchedules(updatePersonalSchedules)
                            .then(groupRepository.save(group))
                            .then(Mono.just(groupSchedule))
                    }
            }.map(::groupScheduleToDao)
            .onErrorMap {
                when(it){
                    is NotFoundPersonalScheduleException -> GroupScheduleException(ErrorCode.PERSONAL_SCHEDULE_CANT_FIND)
                    else -> it
                }
            }
    }

    override fun deletePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        personalScheduleIdList: List<ObjectId>
    ): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                group ->
                group.findGroupScheduleById(scheduleId)
                    .flatMap {
                        groupSchedule ->
                        val userPersonalSchedule = groupSchedule.findGroupScheduleByUserId(userId)

                        userPersonalSchedule.deletePersonalSchedulesById(personalScheduleIdList)
                            .then(groupRepository.save(group))
                            .then(Mono.just(groupSchedule))
                    }
            }.map(::groupScheduleToDao)
            .onErrorMap {
                when(it){
                    is NotFoundPersonalScheduleException -> GroupScheduleException(ErrorCode.PERSONAL_SCHEDULE_CANT_FIND)
                    else -> it
                }
            }
    }

    override fun changeStateToConfirmSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        confirmScheduleDao: ConfirmScheduleDao
    ): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .flatMap {
                group ->
                group.updateGroupScheduleState(
                    scheduleId = scheduleId,
                    state = GroupScheduleStateEnum.find(confirmScheduleDao.state.value)!!,
                    userId = userId,
                    confirmedStartDate = confirmScheduleDao.startTime,
                    confirmedEndDate = confirmScheduleDao.endTime
                )
                    .flatMap {
                        groupSchedule ->
                        groupRepository.save(group)
                            .map{groupSchedule}
                    }
            }.map{
                groupScheduleToDao(it)
            }
    }


    private fun personalSchedulesDaoToDocumentList(personalSchedulesDao: PersonalSchedulesDao): List<PersonalSchedule>{
        return personalSchedulesDao.personalSchedules.map{
            personalScheduleDao -> personalScheduleDaoToDocument(personalScheduleDao)
        }
    }

    private fun personalScheduleDaoToDocument(personalScheduleDao: PersonalScheduleDao): PersonalSchedule =
        PersonalSchedule(
            id = personalScheduleDao.id ?: ObjectId.get(),
            startTime = personalScheduleDao.startTime,
            endTime = personalScheduleDao.endTime
        )


    private fun groupScheduleToDao(groupSchedule: GroupSchedule): GroupScheduleDao =
        GroupScheduleDao(
            id = groupSchedule.id,
            name = groupSchedule.name,
            startDate = groupSchedule.startDate,
            endDate = groupSchedule.endDate,
            startTime = groupSchedule.startTime,
            endTime = groupSchedule.endTime,
            confirmScheduleDao = ConfirmScheduleDao(
                state = GroupScheduleStateDaoEnum.find(groupSchedule.state.value)!!,
                startTime = groupSchedule.confirmedStartDate,
                endTime = groupSchedule.confirmedEndDate,
                confirmedUser = groupSchedule.confirmedUser
            ),
            personalScheduleMap = groupSchedule.personalScheduleMap.mapValues {
                PersonalSchedulesDao(
                    personalSchedules = it.value.personalSchedules.map{
                        personalScheduleInGroup ->
                        PersonalScheduleDao(
                            id = personalScheduleInGroup.id,
                            startTime = personalScheduleInGroup.startTime,
                            endTime = personalScheduleInGroup.endTime,
                        )
                    }
                )
            },
        )
}
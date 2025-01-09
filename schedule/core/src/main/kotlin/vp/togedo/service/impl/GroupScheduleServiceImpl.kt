package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dao.groupSchedule.*
import vp.togedo.model.documents.group.GroupScheduleElement
import vp.togedo.model.documents.group.IndividualScheduleDocument
import vp.togedo.repository.GroupRepository
import vp.togedo.repository.IndividualScheduleRepository
import vp.togedo.service.GroupScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.GroupException
import vp.togedo.util.error.exception.GroupScheduleException
import vp.togedo.util.exception.groupSchedule.CantCreateMoreScheduleException
import vp.togedo.util.exception.groupSchedule.NotFoundGroupScheduleException

@Service
class GroupScheduleServiceImpl(
    private val groupRepository: GroupRepository,
    private val individualScheduleRepository: IndividualScheduleRepository
): GroupScheduleService {

    override fun createGroupSchedule(groupId: ObjectId, name: String, startDate: String, endDate: String, startTime: String, endTime: String): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId).flatMap{
            group ->
            val groupScheduleElement = group.createGroupSchedule(
                    name = name,
                    startDate = startDate,
                    endDate = endDate,
                    startTime = startTime,
                    endTime = endTime
                )
            groupRepository.save(group).then(
                Mono.just(groupScheduleElement)
            )

        }.map(::groupScheduleToDao).onErrorMap {
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
                        confirmScheduleDao = null
                    )
                }
        }

    override fun readGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .map {
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
                val groupSchedule = group.findGroupScheduleById(groupId).updateGroupScheduleElement(
                    name = groupScheduleDao.name,
                    startDate = groupScheduleDao.startDate,
                    endDate = groupScheduleDao.endDate,
                    startTime = groupScheduleDao.startTime,
                    endTime = groupScheduleDao.endTime
                )
                groupRepository.save(group)
                    .thenReturn(groupSchedule)
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
                it.deleteGroupScheduleElementById(scheduleId)
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
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao> {
        return individualScheduleRepository.findById(groupId)
            .flatMap { individualScheduleDocument ->

                val userIndividualSchedule = individualScheduleDocument.findIndividualScheduleById(userId = userId)

                individualScheduleListDao.individualSchedules.forEach { individualScheduleDao ->
                    userIndividualSchedule.addIndividualSchedule(
                        startTime = individualScheduleDao.startTime,
                        endTime = individualScheduleDao.endTime
                    )
                }

                individualScheduleRepository.save(individualScheduleDocument)
            }
            .map(::individualScheduleToDao)
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
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao> {
        return individualScheduleRepository.findById(groupId)
            .flatMap { individualScheduleDocument ->

                val userIndividualSchedule = individualScheduleDocument.findIndividualScheduleById(userId = userId)

                individualScheduleListDao.individualSchedules.forEach { individualScheduleDao ->
                    userIndividualSchedule.removeIndividualScheduleById(individualScheduleDao.id!!)
                    userIndividualSchedule.addIndividualSchedule(
                        startTime = individualScheduleDao.startTime,
                        endTime = individualScheduleDao.endTime
                    )
                }

                individualScheduleRepository.save(individualScheduleDocument)
            }
            .map(::individualScheduleToDao)
            .onErrorMap {
                when(it){
                    is GroupScheduleException -> GroupScheduleException(ErrorCode.USER_NOT_JOINED_SCHEDULE)
                    else -> it
                }
            }
    }

    override fun deletePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleIdList: List<ObjectId>
    ): Mono<IndividualScheduleDao> {
        return individualScheduleRepository.findById(groupId)
            .flatMap { individualScheduleDocument ->

                val userIndividualSchedule = individualScheduleDocument.findIndividualScheduleById(userId = userId)

                individualScheduleIdList.forEach {
                    userIndividualSchedule.removeIndividualScheduleById(it)
                }

                individualScheduleRepository.save(individualScheduleDocument)
            }
            .map(::individualScheduleToDao)
            .onErrorMap {
                when(it){
                    is GroupScheduleException -> GroupScheduleException(ErrorCode.USER_NOT_JOINED_SCHEDULE)
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
                val groupScheduleElement = group.findGroupScheduleById(groupScheduleId = scheduleId)
                    .requestConfirmSchedule(
                        confirmedStartDate = confirmScheduleDao.startTime!!,
                        confirmedEndDate = confirmScheduleDao.endTime!!,
                        userId = userId,
                    )
                groupRepository.save(group)
                    .thenReturn(groupScheduleElement)
            }.map{
                groupScheduleToDao(it)
            }
    }

    override fun acceptConfirmGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
    ): Mono<GroupScheduleDao>{
        return groupRepository.findById(groupId)
            .flatMap{
                group ->
                val groupScheduleElement = group.findGroupScheduleById(groupScheduleId = scheduleId).checkRequestedSchedule(
                    userId = userId,
                    isApprove = true
                )
                groupRepository.save(group)
                    .thenReturn(groupScheduleElement)
            }.map(::groupScheduleToDao)
    }

    override fun rejectConfirmGroupSchedule(groupId: ObjectId, scheduleId: ObjectId, userId: ObjectId): Mono<GroupScheduleDao> {
        return groupRepository.findById(groupId)
            .flatMap{
                    group ->
                val groupScheduleElement = group.findGroupScheduleById(groupScheduleId = scheduleId).checkRequestedSchedule(
                    userId = userId,
                    isApprove = false
                )
                groupRepository.save(group)
                    .thenReturn(groupScheduleElement)
            }.map(::groupScheduleToDao)
    }

    private fun groupScheduleToDao(groupScheduleElement: GroupScheduleElement): GroupScheduleDao =
        GroupScheduleDao(
            id = groupScheduleElement.id,
            name = groupScheduleElement.name,
            startDate = groupScheduleElement.startDate,
            endDate = groupScheduleElement.endDate,
            startTime = groupScheduleElement.startTime,
            endTime = groupScheduleElement.endTime,
            confirmScheduleDao = ConfirmScheduleDao(
                state = GroupScheduleStateDaoEnum.find(groupScheduleElement.state.value)!!,
                startTime = groupScheduleElement.confirmedStartDate,
                endTime = groupScheduleElement.confirmedEndDate,
                confirmedUser = groupScheduleElement.confirmedUser
            )
        )

    private fun individualScheduleToDao(individualScheduleDocument: IndividualScheduleDocument): IndividualScheduleDao {
        val individualScheduleDao = IndividualScheduleDao()
        individualScheduleDocument.individualScheduleMap.forEach { (userId, individualScheduleList) ->
            individualScheduleDao.individualScheduleDaoMap[userId] = IndividualScheduleListDao(
                individualScheduleList.individualSchedules.map {
                    IndividualScheduleElementDao(
                        id = it.id,
                        startTime = it.startTime,
                        endTime = it.endTime,
                    )
                }
            )
        }
        return individualScheduleDao
    }
}
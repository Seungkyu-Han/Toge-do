package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.connector.GroupScheduleConnector
import vp.togedo.data.dao.groupSchedule.ConfirmScheduleDao
import vp.togedo.data.dao.groupSchedule.GroupScheduleDao
import vp.togedo.data.dao.groupSchedule.GroupScheduleStateDaoEnum
import vp.togedo.data.dao.groupSchedule.PersonalSchedulesDao
import vp.togedo.data.dto.groupSchedule.*
import vp.togedo.service.GroupScheduleService
import vp.togedo.service.KafkaService

@Service
class GroupScheduleConnectorImpl(
    private val groupScheduleService: GroupScheduleService,
    private val kafkaService: KafkaService
): GroupScheduleConnector {

    override fun createGroupSchedule(
        userId: ObjectId,
        groupId: ObjectId,
        name: String,
        startDate: Long,
        endDate: Long,
        startTime: String,
        endTime: String,
    ): Mono<GroupScheduleDetailDto> {
        return groupScheduleService.createGroupSchedule(
            groupId = groupId,
            name = name,
            startDate = startDate,
            endDate = endDate,
            startTime = startTime,
            endTime = endTime,
        ).doOnNext{
            groupScheduleDao ->
            groupScheduleDao.personalScheduleMap!!.keys.forEach {
                key ->
                if (key != userId){
                    kafkaService.publishCreateGroupScheduleEvent(key, groupScheduleDao).subscribe()
                }
            }
        }.map{groupScheduleDaoToDto(it)}

    }

    override fun readGroupSchedules(groupId: ObjectId): Flux<GroupScheduleDto> {
        return groupScheduleService.readGroupSchedules(groupId = groupId)
            .map{
                GroupScheduleDto(
                    id = it.id.toString(),
                    name = it.name,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    startTime = it.startTime,
                    endTime = it.startTime,
                )
            }
    }

    override fun readGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<GroupScheduleDetailDto> =
        groupScheduleService.readGroupSchedule(
            groupId = groupId, scheduleId = scheduleId
        ).map{
            groupScheduleDaoToDto(it)
        }

    override fun updateGroupSchedule(updateGroupScheduleReqDto: UpdateGroupScheduleReqDto): Mono<GroupScheduleDetailDto> =
        groupScheduleService.updateGroupSchedule(
            groupId = ObjectId(updateGroupScheduleReqDto.groupId),
            groupScheduleDao = GroupScheduleDao(
                id = ObjectId(updateGroupScheduleReqDto.scheduleId),
                name = updateGroupScheduleReqDto.name,
                startDate = updateGroupScheduleReqDto.startDate,
                endDate = updateGroupScheduleReqDto.endDate,
                startTime = updateGroupScheduleReqDto.startTime,
                endTime = updateGroupScheduleReqDto.endTime,
                personalScheduleMap = null,
                confirmScheduleDao = null
            )
        ).map{
            groupScheduleDaoToDto(it)
        }

    override fun deleteGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<Void> =
        groupScheduleService.deleteGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId
        )

    override fun createPersonalScheduleInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        personalSchedulesDao: PersonalSchedulesDao
    ): Mono<GroupScheduleDetailDto> {
        return groupScheduleService.addPersonalSchedulesInGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId,
            personalSchedulesDao = personalSchedulesDao
        ).map{
            groupScheduleDaoToDto(it)
        }
    }

    override fun updatePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        personalSchedulesDao: PersonalSchedulesDao
    ): Mono<GroupScheduleDetailDto> {
        return groupScheduleService.updatePersonalSchedulesInGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId,
            personalSchedulesDao = personalSchedulesDao
        ).map{
            groupScheduleDaoToDto(it)
        }
    }

    override fun deletePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        personalScheduleIdList: List<ObjectId>
    ): Mono<GroupScheduleDetailDto> =
        groupScheduleService.deletePersonalSchedulesInGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId,
            personalScheduleIdList = personalScheduleIdList
        ).map(::groupScheduleDaoToDto)

    override fun createSuggestGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        startTime: String,
        endTime: String
    ): Mono<GroupScheduleDetailDto> =
        groupScheduleService.changeStateToConfirmSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId,
            confirmScheduleDao = ConfirmScheduleDao(
                startTime = startTime,
                endTime = endTime,
                state = GroupScheduleStateDaoEnum.REQUESTED,
                confirmedUser = null)
        ).publishOn(Schedulers.boundedElastic())
            .doOnNext {
                groupScheduleDao ->
                Flux.fromIterable(groupScheduleDao.personalScheduleMap!!.keys)
                    .publishOn(Schedulers.boundedElastic())
                    .map{
                        userIdInGroup ->
                        if(userId != userIdInGroup)
                            kafkaService.publishSuggestConfirmScheduleEvent(
                                userId.toString(), groupScheduleDao
                            ).subscribe()
                    }.subscribe()
        }.map{groupScheduleDaoToDto(it)}

    override fun acceptConfirmGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId
    ): Mono<GroupScheduleDetailDto> =
        groupScheduleService.acceptConfirmGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId
        ).publishOn(Schedulers.boundedElastic())
            .doOnNext {
            groupScheduleDao ->
            if(groupScheduleDao.confirmScheduleDao!!.state == GroupScheduleStateDaoEnum.CONFIRMED){
                Flux.fromIterable(groupScheduleDao.personalScheduleMap!!.keys)
                    .map{
                        userIdInGroup ->
                        kafkaService.publishSuggestConfirmScheduleEvent(
                            userIdInGroup.toString(), groupScheduleDao
                        )
                    }.subscribe()
            }
        }.map(::groupScheduleDaoToDto)

    private fun groupScheduleDaoToDto(groupScheduleDao: GroupScheduleDao): GroupScheduleDetailDto = GroupScheduleDetailDto(
        id = groupScheduleDao.id.toString(),
        name = groupScheduleDao.name,
        startDate = groupScheduleDao.startDate,
        endDate = groupScheduleDao.endDate,
        startTime = groupScheduleDao.startTime,
        endTime = groupScheduleDao.endTime,
        personalScheduleMap = groupScheduleDao.personalScheduleMap!!.map{
                (key, value) ->
            key.toString() to PersonalSchedulesDto(
                personalSchedules = value.personalSchedules.map{
                    personalSchedule ->
                    PersonalScheduleDto(
                        id = personalSchedule.id.toString(),
                        startTime = personalSchedule.startTime,
                        endTime = personalSchedule.endTime
                    )
                }
            )
        }.toMap(),
        confirmSchedule = ConfirmSchedule(
            startTime = groupScheduleDao.confirmScheduleDao?.startTime,
            endTime = groupScheduleDao.confirmScheduleDao?.endTime,
            state = groupScheduleDao.confirmScheduleDao!!.state.name,
            confirmedUser = groupScheduleDao.confirmScheduleDao!!.confirmedUser!!.map{it.toString()}
        )
    )

}
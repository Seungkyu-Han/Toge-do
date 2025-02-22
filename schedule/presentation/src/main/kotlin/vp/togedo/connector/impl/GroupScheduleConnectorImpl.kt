package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.connector.GroupScheduleConnector
import vp.togedo.data.dao.groupSchedule.*
import vp.togedo.data.dto.groupSchedule.*
import vp.togedo.kafka.data.groupSchedule.ConfirmScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.CreateGroupScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.SuggestGroupScheduleEventDto
import vp.togedo.kafka.service.GroupScheduleKafkaService
import vp.togedo.service.GroupScheduleService

@Service
class GroupScheduleConnectorImpl(
    private val groupScheduleService: GroupScheduleService,
    private val groupScheduleKafkaService: GroupScheduleKafkaService
): GroupScheduleConnector {

    override fun createGroupSchedule(
        userId: ObjectId,
        groupId: ObjectId,
        name: String,
        startDate: String,
        endDate: String,
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
            groupScheduleDao.members!!.forEach {
                key ->
                if (key != userId){
                    groupScheduleKafkaService.publishCreateGroupScheduleEvent(
                        CreateGroupScheduleEventDto(
                            receiverId = key.toString(),
                            name = groupScheduleDao.name
                        )
                    ).subscribe()
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
                confirmScheduleDao = null,
                members = null
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
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao> {
        return groupScheduleService.addPersonalSchedulesInGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId,
            individualScheduleListDao = individualScheduleListDao
        )
    }

    override fun updatePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao> {
        return groupScheduleService.updatePersonalSchedulesInGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId,
            individualScheduleListDao = individualScheduleListDao
        )
    }

    override fun deletePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleIdList: List<ObjectId>
    ): Mono<IndividualScheduleDao> =
        groupScheduleService.deletePersonalSchedulesInGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId,
            individualScheduleIdList = individualScheduleIdList
        )

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
                Flux.fromIterable(groupScheduleDao.members!!)
                    .publishOn(Schedulers.boundedElastic())
                    .map{
                        userIdInGroup ->
                        if(userId != userIdInGroup)
                            groupScheduleKafkaService.publishSuggestConfirmScheduleEvent(
                                SuggestGroupScheduleEventDto(
                                    receiverId = userIdInGroup.toString(),
                                    name = groupScheduleDao.name
                                )
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
                Flux.fromIterable(groupScheduleDao.members!!)
                    .map{
                        userIdInGroup ->
                        groupScheduleKafkaService.publishConfirmScheduleEvent(
                            ConfirmScheduleEventDto(
                                receiverId = userIdInGroup.toString(),
                                name = groupScheduleDao.name
                            )
                        )
                    }.subscribe()
            }
        }.map(::groupScheduleDaoToDto)

    override fun rejectConfirmGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId
    ): Mono<GroupScheduleDetailDto> =
        groupScheduleService.rejectConfirmGroupSchedule(
            groupId = groupId,
            scheduleId = scheduleId,
            userId = userId
        ).map(::groupScheduleDaoToDto)

    private fun groupScheduleDaoToDto(groupScheduleDao: GroupScheduleDao): GroupScheduleDetailDto = GroupScheduleDetailDto(
        id = groupScheduleDao.id.toString(),
        name = groupScheduleDao.name,
        startDate = groupScheduleDao.startDate,
        endDate = groupScheduleDao.endDate,
        startTime = groupScheduleDao.startTime,
        endTime = groupScheduleDao.endTime,
        confirmSchedule = ConfirmSchedule(
            startTime = groupScheduleDao.confirmScheduleDao?.startTime,
            endTime = groupScheduleDao.confirmScheduleDao?.endTime,
            state = groupScheduleDao.confirmScheduleDao!!.state.name,
            confirmedUser = groupScheduleDao.confirmScheduleDao!!.confirmedUser!!.map{it.toString()}
        )
    )

}
package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.connector.GroupScheduleConnector
import vp.togedo.data.dao.groupSchedule.GroupScheduleDao
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
        endDate: Long
    ): Mono<GroupScheduleDetailDto> {
        return groupScheduleService.createGroupSchedule(
            groupId = groupId,
            name = name,
            startDate = startDate,
            endDate = endDate
        ).doOnNext{
            groupScheduleDao ->
            groupScheduleDao.personalScheduleMap!!.keys.forEach {
                key ->
                if (key != userId){
                    kafkaService.publishCreateGroupScheduleEvent(key, groupScheduleDao).subscribe()
                }
            }
        }.map{groupScheduleDaoToDto(it) }

    }

    override fun readGroupSchedules(groupId: ObjectId): Flux<GroupScheduleDto> {
        return groupScheduleService.readGroupSchedules(groupId = groupId)
            .map{
                GroupScheduleDto(
                    id = it.id.toString(),
                    name = it.name,
                    startDate = it.startDate,
                    endDate = it.endDate
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
                personalScheduleMap = null
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

    private fun groupScheduleDaoToDto(groupScheduleDao: GroupScheduleDao): GroupScheduleDetailDto = GroupScheduleDetailDto(
        id = groupScheduleDao.id.toString(),
        name = groupScheduleDao.name,
        startDate = groupScheduleDao.startDate,
        endDate = groupScheduleDao.endDate,
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
        }.toMap()
    )

}
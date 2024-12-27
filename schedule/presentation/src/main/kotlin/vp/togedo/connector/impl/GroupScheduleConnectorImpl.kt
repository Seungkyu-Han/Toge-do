package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.connector.GroupScheduleConnector
import vp.togedo.data.dao.GroupScheduleDao
import vp.togedo.data.dto.groupSchedule.GroupScheduleDetailDto
import vp.togedo.data.dto.groupSchedule.PersonalScheduleDto
import vp.togedo.data.dto.groupSchedule.PersonalSchedulesDto
import vp.togedo.service.GroupScheduleService
import java.time.LocalDate

@Service
class GroupScheduleConnectorImpl(
    private val groupScheduleService: GroupScheduleService
): GroupScheduleConnector {

    override fun createGroupSchedule(
        groupId: ObjectId,
        name: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Mono<GroupScheduleDetailDto> {
        return groupScheduleService.createGroupSchedule(
            groupId = groupId,
            name = name,
            startDate = startDate,
            endDate = endDate
        ).map{groupScheduleDaoToDto(it) }
    }

    private fun groupScheduleDaoToDto(groupScheduleDao: GroupScheduleDao): GroupScheduleDetailDto = GroupScheduleDetailDto(
        id = groupScheduleDao.id.toString(),
        name = groupScheduleDao.name,
        startDate = groupScheduleDao.startDate,
        endDate = groupScheduleDao.endDate,
        personalScheduleMap = groupScheduleDao.personalScheduleMap.map{
                (key, value) ->
            key.toString() to PersonalSchedulesDto(
                personalSchedules = value.personalSchedules.map{
                    personalSchedule ->
                    PersonalScheduleDto(
                        startTime = personalSchedule.startTime,
                        endTime = personalSchedule.endTime
                    )
                }
            )
        }.toMap()
    )

}
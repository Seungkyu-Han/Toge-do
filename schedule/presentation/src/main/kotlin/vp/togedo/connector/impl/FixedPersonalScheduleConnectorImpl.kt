package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.connector.FixedPersonalScheduleConnector
import vp.togedo.data.dao.ScheduleDao
import vp.togedo.data.dto.fixedPersonalSchedule.CreateFixedReqDto
import vp.togedo.data.dto.fixedPersonalSchedule.UpdateFixedReqDto
import vp.togedo.service.FixedPersonalScheduleService

@Service
class FixedPersonalScheduleConnectorImpl(
    private val fixedPersonalScheduleService: FixedPersonalScheduleService
): FixedPersonalScheduleConnector {

    override suspend fun createFixedSchedule(userId: ObjectId, createFixedReqDtoList: List<CreateFixedReqDto>): List<ScheduleDao> {
        return fixedPersonalScheduleService.createSchedule(
            userId = userId,
            scheduleDaoList = createFixedReqDtoList.map{
                ScheduleDao(
                    scheduleId = null,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    title = it.title,
                    color = it.color
                )
            }
        )
    }

    override suspend fun readFixedSchedule(id: ObjectId): List<ScheduleDao> {
        return fixedPersonalScheduleService.readSchedule(id)
    }

    override suspend fun updateFixedSchedule(id: ObjectId, updateFixedReqDto: UpdateFixedReqDto): List<ScheduleDao> {
        return fixedPersonalScheduleService.modifySchedule(
            userId = id,
            mutableListOf()
        )
    }

    override suspend fun deleteFixedSchedule(userId: ObjectId, scheduleIdList: List<String>) {
        fixedPersonalScheduleService.deleteSchedule(userId, scheduleIdList.map{
            ObjectId(it)
        })
    }
}
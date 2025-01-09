package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.connector.FixedPersonalScheduleConnector
import vp.togedo.data.dao.personalSchedule.FixedScheduleDao
import vp.togedo.data.dto.fixedPersonalSchedule.CreateFixedReqDto
import vp.togedo.data.dto.fixedPersonalSchedule.UpdateFixedReqDto
import vp.togedo.service.FixedPersonalScheduleService

@Service
class FixedPersonalScheduleConnectorImpl(
    private val fixedPersonalScheduleService: FixedPersonalScheduleService
): FixedPersonalScheduleConnector {

    override suspend fun createFixedSchedule(userId: ObjectId, createFixedReqDtoList: List<CreateFixedReqDto>): List<FixedScheduleDao> {
        return fixedPersonalScheduleService.createSchedule(
            userId = userId,
            fixedScheduleDaoList = createFixedReqDtoList.map{
                FixedScheduleDao(
                    scheduleId = null,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    name = it.name,
                    color = it.color
                )
            }
        )
    }

    override suspend fun readFixedSchedule(id: ObjectId): List<FixedScheduleDao> {
        return fixedPersonalScheduleService.readSchedule(id)
    }

    override suspend fun updateFixedSchedule(id: ObjectId, updateFixedReqDtoList: List<UpdateFixedReqDto>): List<FixedScheduleDao> {
        return fixedPersonalScheduleService.modifySchedule(
            userId = id,
            updateFixedReqDtoList.map{
                FixedScheduleDao(
                    scheduleId = ObjectId(it.id),
                    startTime = it.startTime,
                    endTime = it.endTime,
                    name = it.name,
                    color = it.color
                )
            }
        )
    }

    override suspend fun deleteFixedSchedule(userId: ObjectId, fixedScheduleIdList: List<String>) {
        fixedPersonalScheduleService.deleteSchedule(userId, fixedScheduleIdList.map{
            ObjectId(it)
        })
    }
}
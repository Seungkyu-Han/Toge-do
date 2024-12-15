package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.connector.FixedPersonalScheduleConnector
import vp.togedo.data.dao.ScheduleDao
import vp.togedo.data.dto.fixedPersonalSchedule.CreateFixedReqDto
import vp.togedo.data.dto.fixedPersonalSchedule.FixedPersonalScheduleDto
import vp.togedo.data.dto.fixedPersonalSchedule.ReadFixedResDto
import vp.togedo.data.dto.fixedPersonalSchedule.UpdateFixedReqDto
import vp.togedo.service.FixedPersonalScheduleService

@Service
class FixedPersonalScheduleConnectorImpl(
    private val fixedPersonalScheduleService: FixedPersonalScheduleService
): FixedPersonalScheduleConnector {

    override suspend fun createFixedSchedule(userId: ObjectId, createFixedReqDto: CreateFixedReqDto): ScheduleDao {
        return fixedPersonalScheduleService.createSchedule(
            ScheduleDao(
                userId = userId,
                scheduleId = null,
                startTime = createFixedReqDto.startTime,
                endTime = createFixedReqDto.endTime,
                title = createFixedReqDto.title,
                color = createFixedReqDto.color,
            )
        )
    }

    override suspend fun readFixedSchedule(id: ObjectId): ReadFixedResDto {
        val scheduleDaoList = fixedPersonalScheduleService.readSchedule(id)

        return ReadFixedResDto(
            schedules = scheduleDaoList.map{
                FixedPersonalScheduleDto(
                    id = it.scheduleId!!.toString(),
                    startTime = it.startTime,
                    endTime = it.endTime,
                    title = it.title,
                    color = it.color
                )
            }
        )
    }

    override suspend fun updateFixedSchedule(id: ObjectId, updateFixedReqDto: UpdateFixedReqDto): ScheduleDao {
        return fixedPersonalScheduleService.modifySchedule(
            ScheduleDao(
                userId = id,
                scheduleId = ObjectId(updateFixedReqDto.id),
                startTime = updateFixedReqDto.startTime,
                endTime = updateFixedReqDto.endTime,
                title = updateFixedReqDto.title,
                color = updateFixedReqDto.color
            )
        )
    }

    override suspend fun deleteFixedSchedule(userId: ObjectId, scheduleId: ObjectId) {
        fixedPersonalScheduleService.deleteSchedule(userId, scheduleId)
    }
}
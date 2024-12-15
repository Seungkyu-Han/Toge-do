package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.connector.FixedPersonalScheduleConnector
import vp.togedo.data.dto.fixedPersonalSchedule.FixedPersonalScheduleElement
import vp.togedo.data.dto.fixedPersonalSchedule.ReadFixedResDto
import vp.togedo.service.FixedPersonalScheduleService

@Service
class FixedPersonalScheduleConnectorImpl(
    private val fixedPersonalScheduleService: FixedPersonalScheduleService
): FixedPersonalScheduleConnector {

    override suspend fun readFixSchedule(id: ObjectId): ReadFixedResDto {
        val scheduleDaoList = fixedPersonalScheduleService.readSchedule(id)

        return ReadFixedResDto(
            schedules = scheduleDaoList.map{
                FixedPersonalScheduleElement(
                    id = it.scheduleId!!.toString(),
                    startTime = it.startTime,
                    endTime = it.endTime,
                    title = it.title,
                    color = it.color
                )
            }
        )
    }


}
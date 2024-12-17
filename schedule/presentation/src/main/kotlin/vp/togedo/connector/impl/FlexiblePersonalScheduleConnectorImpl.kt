package vp.togedo.connector.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.connector.FlexiblePersonalScheduleConnector
import vp.togedo.data.dao.FlexibleScheduleDao
import vp.togedo.data.dto.flexiblePersonalSchedule.CreateFlexibleReqDto
import vp.togedo.data.dto.flexiblePersonalSchedule.UpdateFlexibleReqDto
import vp.togedo.service.FlexiblePersonalScheduleService

@Service
class FlexiblePersonalScheduleConnectorImpl(
    private val flexiblePersonalScheduleService: FlexiblePersonalScheduleService
): FlexiblePersonalScheduleConnector {

    override suspend fun createFlexibleSchedule(
        userId: ObjectId,
        createFlexibleReqDtoList: List<CreateFlexibleReqDto>
    ): List<FlexibleScheduleDao> {
        return flexiblePersonalScheduleService.createSchedule(
            userId = userId,
            flexibleScheduleDaoList = createFlexibleReqDtoList.map{
                createFlexibleReqDto ->
                FlexibleScheduleDao(
                    scheduleId = null,
                    startTime = createFlexibleReqDto.startTime,
                    endTime = createFlexibleReqDto.endTime,
                    title = createFlexibleReqDto.title,
                    color = createFlexibleReqDto.color,
                    friends = createFlexibleReqDto.friends.map{friend ->
                        ObjectId(friend)
                    }
                )
            }
        )
    }

    override suspend fun readFlexibleSchedule(id: ObjectId): List<FlexibleScheduleDao> {
        return flexiblePersonalScheduleService.readSchedule(id)
    }

    override suspend fun updateFlexibleSchedule(
        id: ObjectId,
        updateFlexibleReqDtoList: List<UpdateFlexibleReqDto>
    ): List<FlexibleScheduleDao> {
        return flexiblePersonalScheduleService.modifySchedule(
            userId = id,
            flexibleScheduleDaoList = updateFlexibleReqDtoList.map{
                FlexibleScheduleDao(
                    scheduleId = ObjectId(it.id),
                    startTime = it.startTime,
                    endTime = it.endTime,
                    title = it.title,
                    color = it.color,
                    friends = it.friends.map{ friend -> ObjectId(friend)}
                )
            }
        )
    }

    override suspend fun deleteFlexibleSchedule(userId: ObjectId, flexibleScheduleIdList: List<String>) {
        flexiblePersonalScheduleService.deleteSchedule(
            userId = userId,
            scheduleIdList = flexibleScheduleIdList.map{
                scheduleId -> ObjectId(scheduleId)
            }
        )
    }
}
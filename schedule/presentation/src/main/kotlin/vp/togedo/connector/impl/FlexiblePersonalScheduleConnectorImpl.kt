package vp.togedo.connector.impl

import org.bson.types.ObjectId
import vp.togedo.connector.FlexiblePersonalScheduleConnector
import vp.togedo.data.dao.FlexibleScheduleDao
import vp.togedo.data.dto.flexiblePersonalSchedule.CreateFlexibleReqDto
import vp.togedo.service.FlexiblePersonalScheduleService

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
}
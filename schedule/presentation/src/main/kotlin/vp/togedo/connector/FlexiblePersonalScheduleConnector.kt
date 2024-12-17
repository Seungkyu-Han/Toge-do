package vp.togedo.connector

import org.bson.types.ObjectId
import vp.togedo.data.dao.FlexibleScheduleDao
import vp.togedo.data.dto.flexiblePersonalSchedule.CreateFlexibleReqDto

interface FlexiblePersonalScheduleConnector {

    /**
     * 사용자의 가변 스케줄 추가하기
     * @param userId 사용자의 objectId
     * @param createFlexibleReqDtoList 가변 스케줄 생성 dto 리스트
     * @return 생성된 스케줄 dao
     */
    suspend fun createFlexibleSchedule(userId: ObjectId, createFlexibleReqDtoList: List<CreateFlexibleReqDto>): List<FlexibleScheduleDao>
}
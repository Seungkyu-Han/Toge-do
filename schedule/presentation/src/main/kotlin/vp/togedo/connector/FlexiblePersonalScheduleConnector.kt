package vp.togedo.connector

import org.bson.types.ObjectId
import vp.togedo.data.dao.FlexibleScheduleDao
import vp.togedo.data.dto.flexiblePersonalSchedule.CreateFlexibleReqDto
import vp.togedo.data.dto.flexiblePersonalSchedule.UpdateFlexibleReqDto

interface FlexiblePersonalScheduleConnector {

    /**
     * 사용자의 가변 스케줄 추가하기
     * @param userId 사용자의 objectId
     * @param createFlexibleReqDtoList 가변 스케줄 생성 dto 리스트
     * @return 생성된 스케줄 dao
     */
    suspend fun createFlexibleSchedule(userId: ObjectId, createFlexibleReqDtoList: List<CreateFlexibleReqDto>): List<FlexibleScheduleDao>

    /**
     * 사용자의 가변 스케줄 가져오기
     * @param id 사용자의 objectId
     */
    suspend fun readFlexibleSchedule(id: ObjectId): List<FlexibleScheduleDao>

    /**
     * 사용자의 가변 스케줄 수정하기
     * @param id 사용자의 objectId
     * @param updateFlexibleReqDtoList 가변 스케줄 생성 dto 리스트
     * @return 생성된 스케줄 dao
     */
    suspend fun updateFlexibleSchedule(id: ObjectId, updateFlexibleReqDtoList: List<UpdateFlexibleReqDto>): List<FlexibleScheduleDao>

    /**
     * 사용자의 가변 스케줄 삭제하기
     * @param userId 사용자의 objectId
     * @param flexibleScheduleIdList 스케줄의 ObjectId 리스트
     */
    suspend fun deleteFlexibleSchedule(userId: ObjectId, flexibleScheduleIdList: List<String>)
}
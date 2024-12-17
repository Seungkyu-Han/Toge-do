package vp.togedo.connector

import org.bson.types.ObjectId
import vp.togedo.data.dao.FixedScheduleDao
import vp.togedo.data.dto.fixedPersonalSchedule.CreateFixedReqDto
import vp.togedo.data.dto.fixedPersonalSchedule.UpdateFixedReqDto

interface FixedPersonalScheduleConnector {

    /**
     * 사용자의 고정 스케줄 추가하기
     * @param userId 사용자의 objectId
     * @param createFixedReqDtoList 고정 스케줄 생성 dto 리스트
     * @return 생성된 스케줄 dao
     */
    suspend fun createFixedSchedule(userId: ObjectId, createFixedReqDtoList: List<CreateFixedReqDto>): List<FixedScheduleDao>

    /**
     * 사용자의 고정 스케줄 가져오기
     * @param id 사용자의 objectId
     */
    suspend fun readFixedSchedule(id: ObjectId): List<FixedScheduleDao>

    /**
     * 사용자의 고정 스케줄 수정하기
     * @param id 사용자의 objectId
     * @param updateFixedReqDtoList 고정 스케줄 생성 dto 리스트
     * @return 생성된 스케줄 dao
     */
    suspend fun updateFixedSchedule(id: ObjectId, updateFixedReqDtoList: List<UpdateFixedReqDto>): List<FixedScheduleDao>

    /**
     * 사용자의 고정 스케줄 삭제하기
     * @param userId 사용자의 objectId
     * @param fixedScheduleIdList 스케줄의 objectId의 리스트
     */
    suspend fun deleteFixedSchedule(userId: ObjectId, fixedScheduleIdList: List<String>)
}
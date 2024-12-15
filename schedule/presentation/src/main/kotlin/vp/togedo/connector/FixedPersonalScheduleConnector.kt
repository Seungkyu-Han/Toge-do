package vp.togedo.connector

import org.bson.types.ObjectId
import vp.togedo.data.dao.ScheduleDao
import vp.togedo.data.dto.fixedPersonalSchedule.CreateFixedReqDto
import vp.togedo.data.dto.fixedPersonalSchedule.ReadFixedResDto

interface FixedPersonalScheduleConnector {

    /**
     * 사용자의 고정 스케줄 추가하기
     * @param userId 사용자의 objectId
     * @param createFixedReqDto 고정 스케줄 생성 dto
     * @return 생성된 스케줄 dao
     */
    suspend fun createFixedSchedule(userId: ObjectId, createFixedReqDto: CreateFixedReqDto): ScheduleDao

    /**
     * 사용자의 고정 스케줄 가져오기
     * @param id 사용자의 아이디
     */
    suspend fun readFixedSchedule(id: ObjectId): ReadFixedResDto
}
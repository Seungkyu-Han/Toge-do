package vp.togedo.connector

import org.bson.types.ObjectId
import vp.togedo.data.dto.fixedPersonalSchedule.ReadFixedResDto

interface FixedPersonalScheduleConnector {

    /**
     * 사용자의 고정 스케줄 가져오기
     * @param id 사용자의 아이디
     */
    suspend fun readFixSchedule(id: ObjectId): ReadFixedResDto
}
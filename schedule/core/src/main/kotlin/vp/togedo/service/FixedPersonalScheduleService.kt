package vp.togedo.service

import org.bson.types.ObjectId
import vp.togedo.data.dao.ScheduleDao

interface FixedPersonalScheduleService {

    /**
     * 해당 유저의 고정 스케줄을 추가
     * @param scheduleDao 스케줄 dao
     * @return id 추가된 scheduleDao
     */
    suspend fun createSchedule(scheduleDao: ScheduleDao): ScheduleDao

    /**
     * 해당 유저의 고정 스케줄을 조회
     * @param userId 조회할 유저의 objectId
     * @return scheduleDao 리스트
     */
    suspend fun readSchedule(userId: ObjectId): List<ScheduleDao>

    /**
     * 해당 스케줄을 수정
     * @param scheduleDao 스케줄 dao
     * @return 변경된 scheduleDao
     */
    suspend fun modifySchedule(scheduleDao: ScheduleDao): ScheduleDao

    /**
     * 해당 스케줄을 삭제
     * @param userId 해당 유저의 id
     * @param scheduleId 삭제하려는 스케줄의 id
     */
    suspend fun deleteSchedule(userId: ObjectId, scheduleId: ObjectId)
}
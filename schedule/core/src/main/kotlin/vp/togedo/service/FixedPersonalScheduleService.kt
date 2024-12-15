package vp.togedo.service

import org.bson.types.ObjectId
import vp.togedo.data.dao.ScheduleDao

interface FixedPersonalScheduleService {

    /**
     * 해당 유저의 고정 스케줄을 추가
     * @param userId 스케줄을 생성할 유저의 objectId
     * @param scheduleDaoList 스케줄 dao 리스트
     * @return id가 추가된 scheduleDao 리스트
     */
    suspend fun createSchedule(userId: ObjectId, scheduleDaoList: List<ScheduleDao>): List<ScheduleDao>

    /**
     * 해당 유저의 고정 스케줄을 조회
     * @param userId 조회할 유저의 objectId
     * @return scheduleDao 리스트
     */
    suspend fun readSchedule(userId: ObjectId): List<ScheduleDao>

    /**
     * 해당 스케줄을 수정
     * @param userId 스케줄을 수정할 유저의 objectId
     * @param scheduleDaoList 스케줄 dao 리스트
     * @return 변경된 scheduleDao 리스트
     */
    suspend fun modifySchedule(userId: ObjectId, scheduleDaoList: List<ScheduleDao>): List<ScheduleDao>

    /**
     * 해당 스케줄을 삭제
     * @param userId 해당 유저의 id
     * @param scheduleIdList 삭제하려는 스케줄의 id 리스트
     */
    suspend fun deleteSchedule(userId: ObjectId, scheduleIdList: List<ObjectId>)
}
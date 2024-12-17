package vp.togedo.service

import org.bson.types.ObjectId
import vp.togedo.data.dao.FlexibleScheduleDao

interface FlexiblePersonalScheduleService {


    /**
     * 해당 유저의 가변 스케줄을 추가
     * @param userId 스케줄을 생성할 유저의 object Id
     * @return id가 추가된 scheduleDaoList
     */
    suspend fun createSchedule(userId: ObjectId, flexibleScheduleDaoList: List<FlexibleScheduleDao>): List<FlexibleScheduleDao>
}
package vp.togedo.service

import vp.togedo.data.dao.ScheduleDao

interface FixedPersonalScheduleService {

    /**
     * 해당 유저의 고정 스케줄을 추가
     * @param scheduleDao 스케줄 dao
     * @return id 추가된 scheduleDao
     */
    suspend fun createSchedule(scheduleDao: ScheduleDao): ScheduleDao
}
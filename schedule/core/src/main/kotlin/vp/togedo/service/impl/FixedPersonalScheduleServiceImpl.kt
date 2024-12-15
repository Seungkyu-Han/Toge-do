package vp.togedo.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.data.dao.ScheduleDao
import vp.togedo.document.FixedPersonalScheduleDocument
import vp.togedo.document.Schedule
import vp.togedo.repository.FixedPersonalScheduleRepository
import vp.togedo.service.FixedPersonalScheduleService

@Service
class FixedPersonalScheduleServiceImpl(
    private val fixedPersonalScheduleRepository: FixedPersonalScheduleRepository
): FixedPersonalScheduleService {

    override suspend fun createSchedule(scheduleDao: ScheduleDao): ScheduleDao {
        val fixedPersonalSchedule: FixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(scheduleDao.userId)
            .awaitSingleOrNull() ?: FixedPersonalScheduleDocument(userId = scheduleDao.userId)

        val schedule = Schedule(
            id = ObjectId.get(),
            startTime = scheduleDao.startTime,
            endTime = scheduleDao.endTime,
            title = scheduleDao.title,
            color = scheduleDao.color
        )

        fixedPersonalSchedule.addSchedule(schedule).awaitSingle()

        fixedPersonalScheduleRepository.save(fixedPersonalSchedule).awaitSingle()

        scheduleDao.scheduleId = schedule.id

        return scheduleDao
    }
}
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
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.ScheduleException

@Service
class FixedPersonalScheduleServiceImpl(
    private val fixedPersonalScheduleRepository: FixedPersonalScheduleRepository
): FixedPersonalScheduleService {

    override suspend fun createSchedule(scheduleDao: ScheduleDao) : ScheduleDao{
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

    override suspend fun readSchedule(userId: ObjectId): List<ScheduleDao> {
        val fixedPersonalSchedule: FixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: FixedPersonalScheduleDocument(userId = userId)

        return fixedPersonalSchedule.schedules.map{
            ScheduleDao(
                userId = userId,
                scheduleId = it.id,
                startTime = it.startTime,
                endTime = it.endTime,
                title = it.title,
                color = it.color
            )
        }
    }

    override suspend fun modifySchedule(scheduleDao: ScheduleDao): ScheduleDao {
        val fixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(scheduleDao.userId).awaitSingleOrNull()
            ?: throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        fixedPersonalScheduleDocument.modifyScheduleById(
            id = scheduleDao.scheduleId!!,
            startTime = scheduleDao.startTime,
            endTime = scheduleDao.endTime,
            title = scheduleDao.title,
            color = scheduleDao.color).awaitSingle()

        fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument).awaitSingle()

        return scheduleDao
    }

    override suspend fun deleteSchedule(userId: ObjectId, scheduleIdList: List<ObjectId>){
        val fixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId).awaitSingleOrNull()
            ?: throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        scheduleIdList.forEach {
            fixedPersonalScheduleDocument.deleteScheduleById(it).awaitSingleOrNull()
        }

        fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument).awaitSingle()
    }
}
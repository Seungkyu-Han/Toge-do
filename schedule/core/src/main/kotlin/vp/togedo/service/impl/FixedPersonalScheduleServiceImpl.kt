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

    override suspend fun createSchedule(userId: ObjectId, scheduleDaoList: List<ScheduleDao>) : List<ScheduleDao>{
        val fixedPersonalSchedule: FixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: FixedPersonalScheduleDocument(userId = userId)

        val createdScheduleDaoList = scheduleDaoList.map {
            scheduleDao ->
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = scheduleDao.startTime,
                endTime = scheduleDao.endTime,
                title = scheduleDao.title,
                color = scheduleDao.color
            )

            fixedPersonalSchedule.addSchedule(schedule).awaitSingle()

            scheduleDao.copy(
                scheduleId = schedule.id
            )
        }

        fixedPersonalScheduleRepository.save(fixedPersonalSchedule).awaitSingle()

        return createdScheduleDaoList
    }

    override suspend fun readSchedule(userId: ObjectId): List<ScheduleDao> {
        val fixedPersonalSchedule: FixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: FixedPersonalScheduleDocument(userId = userId)

        return fixedPersonalSchedule.schedules.map{
            ScheduleDao(
                scheduleId = it.id,
                startTime = it.startTime,
                endTime = it.endTime,
                title = it.title,
                color = it.color
            )
        }
    }

    override suspend fun modifySchedule(userId: ObjectId, scheduleDaoList: List<ScheduleDao>): List<ScheduleDao> {
        val fixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId).awaitSingleOrNull()
            ?: throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        scheduleDaoList.map {
            scheduleDao ->
            fixedPersonalScheduleDocument.modifyScheduleById(
                id = scheduleDao.scheduleId!!,
                startTime = scheduleDao.startTime,
                endTime = scheduleDao.endTime,
                title = scheduleDao.title,
                color = scheduleDao.color).awaitSingle()
        }

        fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument).awaitSingle()

        return scheduleDaoList
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
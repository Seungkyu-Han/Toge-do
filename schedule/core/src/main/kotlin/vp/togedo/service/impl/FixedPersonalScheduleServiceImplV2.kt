package vp.togedo.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import vp.togedo.data.dao.FixedScheduleDao
import vp.togedo.document.PersonalScheduleDocument
import vp.togedo.document.Schedule
import vp.togedo.repository.PersonalScheduleRepository
import vp.togedo.service.FixedPersonalScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.ScheduleException
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException

class FixedPersonalScheduleServiceImplV2(
    private val personalScheduleRepository: PersonalScheduleRepository
): FixedPersonalScheduleService {

    override suspend fun createSchedule(
        userId: ObjectId,
        fixedScheduleDaoList: List<FixedScheduleDao>
    ): List<FixedScheduleDao> {
        val fixedPersonalSchedule: PersonalScheduleDocument = personalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: PersonalScheduleDocument(userId = userId)

        try{
            val createdScheduleDaoList = fixedScheduleDaoList.map{
                fixedScheduleDao ->

                val fixedSchedule = Schedule(
                    startTime = fixedScheduleDao.startTime,
                    endTime = fixedScheduleDao.endTime,
                    title = fixedScheduleDao.title,
                    color = fixedScheduleDao.color
                )

                fixedPersonalSchedule.addFixedSchedule(fixedSchedule).awaitSingle()

                fixedScheduleDao.copy(
                    scheduleId = fixedSchedule.id
                )
            }

            personalScheduleRepository.save(fixedPersonalSchedule).awaitSingle()
            return createdScheduleDaoList
        }
        catch(e: ConflictScheduleException){
            throw ScheduleException(ErrorCode.SCHEDULE_CONFLICT)
        }
        catch(e: EndTimeBeforeStartTimeException){
            throw ScheduleException(ErrorCode.END_TIME_BEFORE_START_TIME)
        }
        catch(e: InvalidTimeException){
            throw ScheduleException(ErrorCode.BAD_SCHEDULE_TIME)
        }
    }

    override suspend fun readSchedule(userId: ObjectId): List<FixedScheduleDao> {
        TODO("Not yet implemented")
    }

    override suspend fun modifySchedule(
        userId: ObjectId,
        fixedScheduleDaoList: List<FixedScheduleDao>
    ): List<FixedScheduleDao> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSchedule(userId: ObjectId, scheduleIdList: List<ObjectId>) {
        TODO("Not yet implemented")
    }
}
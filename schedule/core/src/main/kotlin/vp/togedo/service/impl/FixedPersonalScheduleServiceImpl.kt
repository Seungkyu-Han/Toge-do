package vp.togedo.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.data.dao.FixedScheduleDao
import vp.togedo.document.FixedPersonalScheduleDocument
import vp.togedo.document.FixedSchedule
import vp.togedo.repository.FixedPersonalScheduleRepository
import vp.togedo.service.FixedPersonalScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.ScheduleException
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException
import vp.togedo.util.exception.ScheduleNotFoundException

@Service
class FixedPersonalScheduleServiceImpl(
    private val fixedPersonalScheduleRepository: FixedPersonalScheduleRepository
): FixedPersonalScheduleService {

    override suspend fun createSchedule(userId: ObjectId, fixedScheduleDaoList: List<FixedScheduleDao>) : List<FixedScheduleDao>{
        val fixedPersonalSchedule: FixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: FixedPersonalScheduleDocument(userId = userId)

        try{
            val createdScheduleDaoList = fixedScheduleDaoList.map {
                scheduleDao ->
                val fixedSchedule = FixedSchedule(
                    id = ObjectId.get(),
                    startTime = scheduleDao.startTime,
                    endTime = scheduleDao.endTime,
                    title = scheduleDao.title,
                    color = scheduleDao.color
                )

                fixedPersonalSchedule.addSchedule(fixedSchedule).awaitSingle()

                scheduleDao.copy(
                    scheduleId = fixedSchedule.id
                )
            }

            fixedPersonalScheduleRepository.save(fixedPersonalSchedule).awaitSingle()

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
        val fixedPersonalSchedule: FixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: FixedPersonalScheduleDocument(userId = userId)

        return fixedPersonalSchedule.fixedSchedules.map{
            FixedScheduleDao(
                scheduleId = it.id,
                startTime = it.startTime,
                endTime = it.endTime,
                title = it.title,
                color = it.color
            )
        }
    }

    override suspend fun modifySchedule(userId: ObjectId, fixedScheduleDaoList: List<FixedScheduleDao>): List<FixedScheduleDao> {
        val fixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId).awaitSingleOrNull()
            ?: throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        try {
            fixedScheduleDaoList.map { scheduleDao ->
                fixedPersonalScheduleDocument.modifyScheduleById(
                    id = scheduleDao.scheduleId!!,
                    startTime = scheduleDao.startTime,
                    endTime = scheduleDao.endTime,
                    title = scheduleDao.title,
                    color = scheduleDao.color
                ).awaitSingle()
            }

            fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument).awaitSingle()

            return fixedScheduleDaoList
        }
        catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }
    }

    override suspend fun deleteSchedule(userId: ObjectId, scheduleIdList: List<ObjectId>){
        val fixedPersonalScheduleDocument = fixedPersonalScheduleRepository.findByUserId(userId).awaitSingleOrNull()
            ?: throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        try {
            scheduleIdList.forEach {
                fixedPersonalScheduleDocument.deleteScheduleById(it).awaitSingleOrNull()
            }
        }catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }

        fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument).awaitSingle()
    }
}
package vp.togedo.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
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
import vp.togedo.util.exception.ScheduleNotFoundException

@Service
class FixedPersonalScheduleServiceImplV2(
    private val personalScheduleRepository: PersonalScheduleRepository
): FixedPersonalScheduleService {

    override suspend fun createSchedule(
        userId: ObjectId,
        fixedScheduleDaoList: List<FixedScheduleDao>
    ): List<FixedScheduleDao> {
        val personalSchedule: PersonalScheduleDocument = personalScheduleRepository.findByUserId(userId)
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

                personalSchedule.addFixedSchedule(fixedSchedule).awaitSingle()

                fixedScheduleDao.copy(
                    scheduleId = fixedSchedule.id
                )
            }

            personalScheduleRepository.save(personalSchedule).awaitSingle()
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
        val personalSchedule = personalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: PersonalScheduleDocument(userId = userId)

        return personalSchedule.fixedSchedules.map{
            FixedScheduleDao(
                scheduleId = it.id,
                startTime = it.startTime,
                endTime = it.endTime,
                title = it.title,
                color = it.color
            )
        }
    }

    override suspend fun modifySchedule(
        userId: ObjectId,
        fixedScheduleDaoList: List<FixedScheduleDao>
    ): List<FixedScheduleDao> {
        val personalSchedule = personalScheduleRepository.findByUserId(userId).awaitSingleOrNull()
            ?: throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        try{
            fixedScheduleDaoList.forEach {
                scheduleDao ->
                personalSchedule.modifyFixedSchedule(
                    Schedule(
                        id = scheduleDao.scheduleId!!,
                        startTime = scheduleDao.startTime,
                        endTime = scheduleDao.endTime,
                        title = scheduleDao.title,
                        color = scheduleDao.color
                    )
                ).awaitSingle()
            }
            personalScheduleRepository.save(personalSchedule).awaitSingle()

            return fixedScheduleDaoList
        }
        catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }
    }

    override suspend fun deleteSchedule(userId: ObjectId, scheduleIdList: List<ObjectId>) {
        val personalSchedule = personalScheduleRepository.findByUserId(userId).awaitSingleOrNull()
            ?: throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)

        try{
            scheduleIdList.forEach {
                personalSchedule.deleteFixedScheduleById(it).awaitSingleOrNull()
            }
        }catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }

        personalScheduleRepository.save(personalSchedule).awaitSingle()
    }
}
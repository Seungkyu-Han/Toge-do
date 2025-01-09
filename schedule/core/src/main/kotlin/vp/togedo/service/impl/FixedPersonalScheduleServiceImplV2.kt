package vp.togedo.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.data.dao.personalSchedule.FixedScheduleDao
import vp.togedo.model.documents.personalSchedule.PersonalScheduleDocument
import vp.togedo.model.documents.personalSchedule.PersonalScheduleElement
import vp.togedo.repository.PersonalScheduleRepository
import vp.togedo.service.FixedPersonalScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.ScheduleException
import vp.togedo.util.exception.schedule.ConflictScheduleException
import vp.togedo.util.exception.schedule.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.schedule.InvalidTimeException
import vp.togedo.util.exception.schedule.ScheduleNotFoundException

@Service
class FixedPersonalScheduleServiceImplV2(
    private val personalScheduleRepository: PersonalScheduleRepository
): FixedPersonalScheduleService {

    override suspend fun createSchedule(
        userId: ObjectId,
        fixedScheduleDaoList: List<FixedScheduleDao>
    ): List<FixedScheduleDao> {
        val personalSchedule: PersonalScheduleDocument = personalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: PersonalScheduleDocument(id = userId)

        try{
            val createdScheduleDaoList = fixedScheduleDaoList.map{
                fixedScheduleDao ->

                val fixedSchedule = PersonalScheduleElement(
                    id = ObjectId.get(),
                    startTime = fixedScheduleDao.startTime,
                    endTime = fixedScheduleDao.endTime,
                    name = fixedScheduleDao.name,
                    color = fixedScheduleDao.color
                )

                personalSchedule.addFixedPersonalScheduleElement(fixedSchedule)

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
            .awaitSingleOrNull() ?: PersonalScheduleDocument(id = userId)

        return personalSchedule.fixedSchedules.map{
            FixedScheduleDao(
                scheduleId = it.id,
                startTime = it.startTime,
                endTime = it.endTime,
                name = it.name,
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
                personalSchedule.modifyFixedPersonalScheduleElement(
                    PersonalScheduleElement(
                        id = scheduleDao.scheduleId!!,
                        startTime = scheduleDao.startTime,
                        endTime = scheduleDao.endTime,
                        name = scheduleDao.name,
                        color = scheduleDao.color
                    )
                )
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
                personalSchedule.deleteFixedPersonalScheduleElementById(it)
            }
        }catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }

        personalScheduleRepository.save(personalSchedule).awaitSingle()
    }
}
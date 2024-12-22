package vp.togedo.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.data.dao.FlexibleScheduleDao
import vp.togedo.document.PersonalScheduleDocument
import vp.togedo.document.Schedule
import vp.togedo.repository.PersonalScheduleRepository
import vp.togedo.service.FlexiblePersonalScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.ScheduleException
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException
import vp.togedo.util.exception.ScheduleNotFoundException

@Service
class FlexiblePersonalScheduleImplV2(
    private val personalScheduleRepository: PersonalScheduleRepository
): FlexiblePersonalScheduleService{

    override suspend fun createSchedule(
        userId: ObjectId,
        flexibleScheduleDaoList: List<FlexibleScheduleDao>
    ): List<FlexibleScheduleDao> {
        println(userId)
        val personalSchedule = personalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: PersonalScheduleDocument(userId = userId)

        try{
            val createdScheduleDaoList = flexibleScheduleDaoList.map {
                flexibleScheduleDao ->
                val schedule = Schedule(
                    id = ObjectId.get(),
                    startTime = flexibleScheduleDao.startTime,
                    endTime = flexibleScheduleDao.endTime,
                    title = flexibleScheduleDao.title,
                    color = flexibleScheduleDao.color
                )

                personalSchedule.addFlexibleSchedule(schedule).awaitSingle()

                flexibleScheduleDao.copy(
                    scheduleId = schedule.id
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

    override suspend fun readSchedule(userId: ObjectId): List<FlexibleScheduleDao> {
        println(userId)
        val personalSchedule = personalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: PersonalScheduleDocument(userId = userId)

        return personalSchedule.flexibleSchedules.map{
            FlexibleScheduleDao(
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
        flexibleScheduleDaoList: List<FlexibleScheduleDao>
    ): List<FlexibleScheduleDao> {
        val personalSchedule = personalScheduleRepository.findByUserId(userId).awaitSingleOrNull() ?:
        throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        try{
            flexibleScheduleDaoList.forEach {
                personalSchedule.modifyFlexibleSchedule(
                    Schedule(
                        id = it.scheduleId!!,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        title = it.title,
                        color = it.color,
                    )
                ).awaitSingle()
            }

            personalScheduleRepository.save(personalSchedule).awaitSingle()

            return flexibleScheduleDaoList
        }catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }
    }

    override suspend fun deleteSchedule(userId: ObjectId, scheduleIdList: List<ObjectId>) {
        val personalSchedule = personalScheduleRepository.findByUserId(userId).awaitSingleOrNull() ?:
            throw ScheduleException(ErrorCode.SCHEDULE_INFO_CANT_FIND)

        try{
            scheduleIdList.forEach {
                personalSchedule.deleteFlexibleScheduleById(it).awaitSingle()
            }
        }catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }

        personalScheduleRepository.save(personalSchedule).awaitSingle()
    }
}
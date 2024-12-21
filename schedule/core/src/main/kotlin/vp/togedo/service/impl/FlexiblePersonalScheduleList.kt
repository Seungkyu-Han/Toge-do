package vp.togedo.service.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
import vp.togedo.data.dao.FlexibleScheduleDao
import vp.togedo.document.FlexiblePersonalScheduleDocument
import vp.togedo.document.FlexibleSchedule
import vp.togedo.repository.FlexiblePersonalScheduleRepository
import vp.togedo.service.FlexiblePersonalScheduleService
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.ScheduleException
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException
import vp.togedo.util.exception.ScheduleNotFoundException

class FlexiblePersonalScheduleList(
    private val flexiblePersonalScheduleRepository: FlexiblePersonalScheduleRepository
): FlexiblePersonalScheduleService {

    override suspend fun createSchedule(
        userId: ObjectId,
        flexibleScheduleDaoList: List<FlexibleScheduleDao>
    ): List<FlexibleScheduleDao> {
        val flexiblePersonalSchedule = flexiblePersonalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: FlexiblePersonalScheduleDocument(userId = userId)

        try{
            val createdScheduleDaoList = flexibleScheduleDaoList.map {
                    scheduleDao ->
                val fixedSchedule = FlexibleSchedule(
                    id = ObjectId.get(),
                    startTime = scheduleDao.startTime,
                    endTime = scheduleDao.endTime,
                    title = scheduleDao.title,
                    color = scheduleDao.color,
                    friends = scheduleDao.friends,
                )

                flexiblePersonalSchedule.addSchedule(fixedSchedule).awaitSingle()

                scheduleDao.copy(
                    scheduleId = fixedSchedule.id
                )
            }

            flexiblePersonalScheduleRepository.save(flexiblePersonalSchedule).awaitSingle()

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
        val flexiblePersonalSchedule = flexiblePersonalScheduleRepository.findByUserId(userId)
            .awaitSingleOrNull() ?: FlexiblePersonalScheduleDocument(userId = userId)

        return flexiblePersonalSchedule.flexibleSchedules.map{
            FlexibleScheduleDao(
                scheduleId = it.id,
                startTime = it.startTime,
                endTime = it.endTime,
                title = it.title,
                color = it.color,
                friends = it.friends,
            )
        }
    }

    override suspend fun modifySchedule(
        userId: ObjectId,
        flexibleScheduleDaoList: List<FlexibleScheduleDao>
    ): List<FlexibleScheduleDao> {
        val flexiblePersonalScheduleDocument = flexiblePersonalScheduleRepository.findByUserId(userId).awaitSingleOrNull() ?:
        throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)

        try{
            flexibleScheduleDaoList.forEach{
                flexiblePersonalScheduleDocument.modifyScheduleById(
                    id = it.scheduleId!!,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    title = it.title,
                    color = it.color,
                    friends = it.friends,
                ).awaitSingle()
            }

            flexiblePersonalScheduleRepository.save(flexiblePersonalScheduleDocument).awaitSingle()

            return flexibleScheduleDaoList
        }catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }
    }

    override suspend fun deleteSchedule(userId: ObjectId, scheduleIdList: List<ObjectId>) {
        val flexiblePersonalScheduleDocument = flexiblePersonalScheduleRepository.findByUserId(userId).awaitSingleOrNull() ?:
        throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)

        try{
            scheduleIdList.forEach{
                flexiblePersonalScheduleDocument.deleteScheduleById(it).awaitSingle()
            }
        }catch(e: ScheduleNotFoundException){
            throw ScheduleException(ErrorCode.SCHEDULE_NOT_FOUND)
        }

        flexiblePersonalScheduleRepository.save(flexiblePersonalScheduleDocument).awaitSingle()
    }
}
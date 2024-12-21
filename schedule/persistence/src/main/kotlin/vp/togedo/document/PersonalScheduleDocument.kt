package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import vp.togedo.enums.ScheduleEnum
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException
import vp.togedo.util.exception.ScheduleNotFoundException

@Document(collection = "personal_schedule")
data class PersonalScheduleDocument(
    @Id
    val id: ObjectId = ObjectId.get(),

    @Indexed(unique = true)
    val userId: ObjectId,

    val fixedSchedules: MutableList<Schedule> = mutableListOf(),

    val flexibleSchedules: MutableList<Schedule> = mutableListOf()
){

    fun deleteFixedScheduleById(scheduleId: ObjectId): Mono<PersonalScheduleDocument> {
        val index = findIndexFixedScheduleById(scheduleId)

        return deleteFixedScheduleByIndex(index)
            .map{
                this
            }
    }

    fun deleteFlexibleScheduleById(scheduleId: ObjectId): Mono<PersonalScheduleDocument> {
        val index = findIndexFlexibleScheduleById(scheduleId)

        return deleteFlexibleScheduleByIndex(index)
            .map{
                this
            }
    }

    fun modifyFixedSchedule(schedule: Schedule): Mono<PersonalScheduleDocument>{
        val scheduleIndex: Int = findIndexFixedScheduleById(schedule.id)
        return deleteFixedScheduleByIndex(scheduleIndex)
            .flatMap{
                originalSchedule ->
                addFixedSchedule(schedule)
                    .doOnError{
                        fixedSchedules.add(scheduleIndex, originalSchedule)
                    }
            }
    }

    fun modifyFlexibleSchedule(schedule: Schedule): Mono<PersonalScheduleDocument>{
        val scheduleIndex: Int = findIndexFlexibleScheduleById(schedule.id)
        return deleteFlexibleScheduleByIndex(scheduleIndex)
            .flatMap{
                    originalSchedule ->
                addFlexibleSchedule(schedule)
                    .doOnError{
                        flexibleSchedules.add(scheduleIndex, originalSchedule)
                    }
            }
    }

    private fun findIndexFixedScheduleById(scheduleId: ObjectId): Int{
        val index = fixedSchedules.indexOfFirst {it.id == scheduleId}
        if (index < 0)
            throw ScheduleNotFoundException("해당 스케줄이 존재하지 않습니다.")
        return index
    }

    private fun findIndexFlexibleScheduleById(scheduleId: ObjectId): Int{
        val index = flexibleSchedules.indexOfFirst {it.id == scheduleId}
        if (index < 0)
            throw ScheduleNotFoundException("해당 스케줄이 존재하지 않습니다.")
        return index
    }

    private fun deleteFixedScheduleByIndex(index: Int): Mono<Schedule>{
        return Mono.fromCallable {
            fixedSchedules.removeAt(index)
        }
    }

    private fun deleteFlexibleScheduleByIndex(index: Int): Mono<Schedule>{
        return Mono.fromCallable {
            fixedSchedules.removeAt(index)
        }
    }

    fun addFixedSchedule(schedule: Schedule): Mono<PersonalScheduleDocument> {
        return Mono.fromCallable {
            this.checkFixedScheduleValidTime(schedule)

            val insertedIndex = this.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)

            this.fixedSchedules.add(insertedIndex, schedule)

            this
        }
    }

    fun addFlexibleSchedule(schedule: Schedule): Mono<PersonalScheduleDocument> {
        return Mono.fromCallable {
            this.checkFlexibleScheduleValidTime(schedule)

            val insertedIndex = this.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

            this.flexibleSchedules.add(insertedIndex, schedule)

            this
        }
    }

    fun getInsertedIndex(schedule: Schedule, scheduleEnum: ScheduleEnum): Int{
        val schedules: MutableList<Schedule> = when(scheduleEnum) {
            ScheduleEnum.FIXED_PERSONAL_SCHEDULE -> fixedSchedules
            ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE -> flexibleSchedules
        }

        if(schedules.size == 0)
            return 0

        val index = schedules.binarySearch(0){
            scheduleElement -> scheduleElement.startTime.compareTo(scheduleElement.startTime)
        }

        if (index >= 0)
            throw ConflictScheduleException("해당 시작시간에 시작하는 스케줄이 있습니다.")

        val insertedIndex = (index + 1) * -1

        //앞의 종료 시간과, 해당 스케줄의 시작 시간을 비교
        if(insertedIndex > 0)
            if(schedules[insertedIndex - 1].endTime >= schedule.startTime)
                throw ConflictScheduleException("전 스케줄이 종료되지 않았습니다.")

        //뒤의 시작 시간과, 해당 스케줄의 종료 시간을 비교
        if(insertedIndex < schedules.size)
            if(schedules[insertedIndex].startTime <= schedule.endTime)
                throw ConflictScheduleException("뒤 스케줄의 시작시간과 충돌합니다.")

        return insertedIndex
    }

    fun checkFixedScheduleValidTime(schedule: Schedule): Boolean{
        return this.checkScheduleValidTime(schedule, 1_00_00, 7_23_59)
    }

    fun checkFlexibleScheduleValidTime(schedule: Schedule): Boolean{
        return this.checkScheduleValidTime(schedule, 2000_00_00, 2100_23_59)
    }

    fun checkScheduleValidTime(schedule: Schedule, startTimeRange: Int, endTimeRange: Int): Boolean {
        this.isStartTimeBefore(schedule)

        try{
            isValidTime(schedule.startTime, startTimeRange, endTimeRange)
        }catch (e: InvalidTimeException){
            throw InvalidTimeException("시작 시간이 ${e.message}")
        }

        try{
            isValidTime(schedule.endTime, startTimeRange, endTimeRange)
        }catch (e: InvalidTimeException){
            throw InvalidTimeException("종료 시간이 ${e.message}")
        }

        return true
    }

    fun isValidTime(time: Int, startTimeRange: Int, endTimeRange: Int): Boolean{
        if(time !in startTimeRange .. endTimeRange)
            throw InvalidTimeException("시간 범위 밖입니다.")

        val hour = (time % 10000) / 100
        if (hour !in 0 .. 23)
            throw InvalidTimeException("hour 범위 밖입니다.")
        val minute = time % 100
        if (minute !in 0..59)
            throw InvalidTimeException("minute 범위 밖입니다.")
        return true
    }

    fun isStartTimeBefore(schedule: Schedule): Boolean{
        if (schedule.startTime < schedule.endTime)
            throw EndTimeBeforeStartTimeException("종료 시간이 시작시간보다 앞입니다.")
        return true
    }
}

data class Schedule(
    @Id
    val id: ObjectId = ObjectId.get(),
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val color: String,
    var friends: List<ObjectId>? = null
)
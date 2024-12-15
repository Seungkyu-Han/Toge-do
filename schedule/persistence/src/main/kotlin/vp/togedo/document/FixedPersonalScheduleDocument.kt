package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException

@Document(collection = "fixed_personal_schedule")
data class FixedPersonalScheduleDocument(
    @Id
    var id: ObjectId? = null,

    @Indexed(unique = true)
    val userId: ObjectId,

    var schedules: MutableList<Schedule> = mutableListOf(),
){
    fun addSchedule(schedule: Schedule): Mono<FixedPersonalScheduleDocument> {
        return Mono.fromCallable {

            //시간 범위 검사
            this.isValidTime(schedule)

            //시작시간과 종료시간 순서 검사
            this.isStartTimeBefore(schedule)

            //스케줄 충돌 체크 및 삽입 인덱스 반환
            val insertedIndex = this.isConflictTime(schedule)

            //해당 인덱스로 삽입
            this.schedules.add(insertedIndex, schedule)

            this
        }
    }

    fun deleteScheduleById(id: ObjectId): Mono<Boolean> {
        return Mono.fromCallable {
            this.schedules.removeIf {
                it.id == id
            }
        }
    }

    fun isValidTime(schedule: Schedule): Boolean {
        try{
            validTimeCheck(schedule.startTime)
        }catch(e: InvalidTimeException){
            throw InvalidTimeException("시작 시간이 ${e.message}")
        }
        try{
            validTimeCheck(schedule.endTime)
        }catch(e: InvalidTimeException){
            throw InvalidTimeException("종료 시간이 ${e.message}")
        }
        return true
    }

    fun validTimeCheck(time: Int): Boolean{
        if(time !in 10000..72359)
            throw InvalidTimeException("week 범위 밖입니다.")
        val hour = (time % 10000) / 100
        if (hour !in 0..23)
            throw InvalidTimeException("hour 범위 밖입니다.")
        val minute = time % 100
        if (minute !in 0..59)
            throw InvalidTimeException("minute 범위 밖입니다.")
        return true
    }

    fun isConflictTime(schedule: Schedule): Int{

        if(schedules.size == 0)
            return 0

        val index = this.schedules.binarySearch(0){
            scheduleElement -> scheduleElement.startTime.compareTo(schedule.startTime)
        }

        if(index >= 0)
            throw ConflictScheduleException("해당 시작시간에 시작하는 스케줄이 있습니다.")

        val insertedIndex = (index + 1) * -1

        //앞의 종료 시간과, 해당 스케줄의 시작 시간을 비교
        if (insertedIndex > 0){
            if(this.schedules[insertedIndex - 1].endTime >= schedule.startTime)
                throw ConflictScheduleException("전 스케줄이 종료되지 않았습니다.")
        }
        //뒤의 시작 시간과, 해당 스케줄의 종료 시간을 비교
        if (insertedIndex < this.schedules.size){
            if(this.schedules[insertedIndex].startTime <= schedule.endTime)
                throw ConflictScheduleException("뒤 스케줄의 시작시간과 충돌합니다.")
        }

        return insertedIndex
    }

    private fun isStartTimeBefore(schedule: Schedule): Boolean {
        if (schedule.startTime > schedule.endTime)
            throw EndTimeBeforeStartTimeException("종료시간이 시작시간보다 앞입니다.")
        return true
    }
}

data class Schedule(
    @Id
    var id: ObjectId = ObjectId.get(),

    var startTime: Int,

    var endTime: Int,

    var title: String,

    var color: String
)
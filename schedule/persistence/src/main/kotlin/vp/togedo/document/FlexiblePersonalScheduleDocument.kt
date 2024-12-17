package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException
import vp.togedo.util.exception.ScheduleNotFoundException

@Document(collection = "flexible_personal_schedule")
data class FlexiblePersonalScheduleDocument(
    @Id
    var id: ObjectId = ObjectId.get(),

    @Indexed(unique = true)
    val userId: ObjectId,

    var flexibleSchedules: MutableList<FlexibleSchedule> = mutableListOf(),
){

    fun addSchedule(flexibleSchedule: FlexibleSchedule): Mono<FlexiblePersonalScheduleDocument> {
        return Mono.fromCallable {

            //시간 범위 검사
            this.isValidTime(flexibleSchedule)

            //스케줄 충돌 체크 및 삽입 인덱스 반환
            val insertedIndex = this.isConflictTime(flexibleSchedule)

            //해당 인덱스로 삽입
            this.flexibleSchedules.add(insertedIndex, flexibleSchedule)

            this
        }
    }

    fun deleteScheduleById(id: ObjectId): Mono<Void> {
        return if(this.flexibleSchedules.removeIf { it.id == id })
            Mono.empty()
        else
            Mono.error(ScheduleNotFoundException("해당 스케줄이 존재하지 않습니다."))
    }

    fun modifyScheduleById(
        id: ObjectId,
        startTime: Int,
        endTime: Int,
        title: String,
        color: String
    ): Mono<FlexiblePersonalScheduleDocument> {
        return Mono.fromCallable {
            val index = this.flexibleSchedules.indexOfFirst { it.id == id }

            if (index < 0)
                throw ScheduleNotFoundException("해당 스케줄이 존재하지 않습니다.")

            val newSchedule = this.flexibleSchedules[index].copy(
                startTime = startTime,
                endTime = endTime,
                title = title,
                color = color
            )

            this.isValidTime(newSchedule)

            this.isConflictTime(newSchedule)

            this.flexibleSchedules[index] = newSchedule

            this
        }
    }

    fun isConflictTime(flexibleSchedule: FlexibleSchedule): Int{

        if(flexibleSchedules.size == 0)
            return 0

        val index = this.flexibleSchedules.binarySearch(0){
                scheduleElement -> scheduleElement.startTime.compareTo(flexibleSchedule.startTime)
        }

        if(index >= 0)
            throw ConflictScheduleException("해당 시작시간에 시작하는 스케줄이 있습니다.")

        val insertedIndex = (index + 1) * -1

        //앞의 종료 시간과, 해당 스케줄의 시작 시간을 비교
        if (insertedIndex > 0){
            if(this.flexibleSchedules[insertedIndex - 1].endTime >= flexibleSchedule.startTime)
                throw ConflictScheduleException("전 스케줄이 종료되지 않았습니다.")
        }
        //뒤의 시작 시간과, 해당 스케줄의 종료 시간을 비교
        if (insertedIndex < this.flexibleSchedules.size){
            if(this.flexibleSchedules[insertedIndex].startTime <= flexibleSchedule.endTime)
                throw ConflictScheduleException("뒤 스케줄의 시작시간과 충돌합니다.")
        }

        return insertedIndex
    }

    fun isValidTime(flexibleSchedule: FlexibleSchedule): Boolean {
        try{
            validTimeCheck(flexibleSchedule.startTime)
        }catch(e: InvalidTimeException){
            throw InvalidTimeException("시작 시간이 ${e.message}")
        }
        try{
            validTimeCheck(flexibleSchedule.endTime)
        }catch(e: InvalidTimeException){
            throw InvalidTimeException("종료 시간이 ${e.message}")
        }
        try{
            isStartTimeBefore(flexibleSchedule)
        }catch(e: EndTimeBeforeStartTimeException){
            throw e
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

    fun isStartTimeBefore(flexibleSchedule: FlexibleSchedule): Boolean {
        if (flexibleSchedule.startTime <= flexibleSchedule.endTime)
            throw EndTimeBeforeStartTimeException("종료시간이 시작시간보다 앞입니다.")
        return true
    }
}

data class FlexibleSchedule(
    @Id
    var id: ObjectId = ObjectId.get(),

    var startTime: Int,

    var endTime: Int,

    var title: String,

    var color: String,

    var friends: List<ObjectId> = mutableListOf()
)
package vp.togedo.model.documents.personalSchedule

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.personalSchedule.EndTimeBeforeStartTimeException
import vp.togedo.model.exception.personalSchedule.TimeIsNotRangeException

@Document(collection = "personal_schedules")
data class PersonalSchedule(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("fixedSchedules")
    val fixedSchedules: List<PersonalScheduleElement>,

    @JsonProperty("flexibleSchedules")
    val flexibleSchedules: List<PersonalScheduleElement>
){
    /**
     * 해당 스케줄의 시간이 범위 내에 있는지 확인
     * @param personalScheduleElement 확인할 요소
     * @param startTimeRange 시작 범위
     * @param endTimeRange 종료 범위
     * @return true
     * @throws TimeIsNotRangeException 유효한 시간 범위가 아님
     */
    fun isTimeRange(
        personalScheduleElement: PersonalScheduleElement,
        startTimeRange: String,
        endTimeRange: String): Boolean{
        if(personalScheduleElement.startTime.length != startTimeRange.length ||
            personalScheduleElement.endTime.length != endTimeRange.length ||
            personalScheduleElement.startTime !in startTimeRange..endTimeRange ||
            personalScheduleElement.endTime !in startTimeRange..endTimeRange){
            throw TimeIsNotRangeException()
        }
        return true
    }

    /**
     * 해당 개인 스케줄 요소의 시작 시간이 종료 시간보다 앞인지 확인
     * @param personalScheduleElement 확인할 요소
     * @return true
     * @throws EndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun isStartTimeBefore(personalScheduleElement: PersonalScheduleElement): Boolean{
        if(personalScheduleElement.startTime.length != personalScheduleElement.endTime.length ||
            personalScheduleElement.startTime > personalScheduleElement.endTime)
            throw EndTimeBeforeStartTimeException()
        return true
    }
}

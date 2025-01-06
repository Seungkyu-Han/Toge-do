package vp.togedo.model.documents.personalSchedule

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.personalSchedule.EndTimeBeforeStartTimeException

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

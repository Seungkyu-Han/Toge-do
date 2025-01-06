package vp.togedo.model.documents.personalSchedule

import org.bson.types.ObjectId
import vp.togedo.model.exception.personalSchedule.PersonalScheduleEndTimeBeforeStartTimeException

data class PersonalScheduleElement(
    val id: ObjectId = ObjectId.get(),
    val startTime: String,
    val endTime: String,
    val name: String,
    val color: String
){
    /**
     * 해당 개인 스케줄 요소의 시작 시간이 종료 시간보다 앞인지 확인
     * @return true
     * @throws PersonalScheduleEndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun isStartTimeBefore(): Boolean{
        if(startTime.length != endTime.length ||
            startTime > endTime)
            throw PersonalScheduleEndTimeBeforeStartTimeException()
        return true
    }

}
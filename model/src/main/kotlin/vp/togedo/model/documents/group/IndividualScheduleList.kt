package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import vp.togedo.model.exception.group.NotFoundIndividualScheduleException

data class IndividualScheduleList(
    @JsonProperty("individualSchedules")
    val individualSchedules: MutableList<IndividualScheduleElement> = mutableListOf(),
){
    /**
     * 개인 일정 목록에서 개인 일정을 추가하는 메서드
     * @param startTime 개인 일정의 시작 시간
     * @param endTime 개인 일정의 종료 시간
     * @return [IndividualScheduleElement] 추가된 개인 일정 요소
     */
    fun addIndividualSchedule(
        startTime: String,
        endTime: String
    ): IndividualScheduleElement {
        val individualSchedule = IndividualScheduleElement(
            startTime = startTime,
            endTime = endTime
        )
        individualSchedules.add(individualSchedule)
        return individualSchedule
    }

    /**
     * 개인 일정 목록에서 개인 일정을 삭제하는 메서드
     * @param individualScheduleId 개인 일정 요소의 object id
     * @throws NotFoundIndividualScheduleException 해당 개인 일정 요소를 찾을 수 없음
     */
    fun removeIndividualScheduleById(individualScheduleId: ObjectId){
        if(!individualSchedules.removeIf { it.id == individualScheduleId })
            throw NotFoundIndividualScheduleException()
    }
}
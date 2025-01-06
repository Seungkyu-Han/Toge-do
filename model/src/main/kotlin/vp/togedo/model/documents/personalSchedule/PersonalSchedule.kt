package vp.togedo.model.documents.personalSchedule

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.personalSchedule.ConflictScheduleException
import vp.togedo.model.exception.personalSchedule.EndTimeBeforeStartTimeException
import vp.togedo.model.exception.personalSchedule.NotFoundPersonalScheduleException
import vp.togedo.model.exception.personalSchedule.TimeIsNotRangeException

@Document(collection = "personal_schedules")
data class PersonalSchedule(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("fixedSchedules")
    val fixedSchedules: MutableList<PersonalScheduleElement>,

    @JsonProperty("flexibleSchedules")
    val flexibleSchedules: MutableList<PersonalScheduleElement>
){
    /**
     * 고정 스케줄에서 아이디를 기준으로 해당 일정을 삭제
     * @param personalScheduleElementId 삭제할 고정 스케줄의 object id
     * @return 고정 스케줄이 삭제된 personal schedule document
     * @throws NotFoundPersonalScheduleException 해당 스케줄이 존재하지 않음
     */
    fun deleteFixedPersonalScheduleElementById(personalScheduleElementId: ObjectId): PersonalSchedule{
        if(!fixedSchedules.removeIf { it.id == personalScheduleElementId })
            throw NotFoundPersonalScheduleException()
        return this
    }

    /**
     * 유동 스케줄에서 아이디를 기준으로 해당 일정을 삭제
     * @param personalScheduleElementId 삭제할 유동 스케줄의 object id
     * @return 유동 스케줄이 삭제된 personal schedule document
     * @throws NotFoundPersonalScheduleException 해당 스케줄이 존재하지 않음
     */
    fun deleteFlexiblePersonalScheduleElementById(personalScheduleElementId: ObjectId): PersonalSchedule{
        if(!flexibleSchedules.removeIf { it.id == personalScheduleElementId })
            throw NotFoundPersonalScheduleException()
        return this
    }

    /**
     * 고정 스케줄에 새로운 고정 일정을 추가
     * @param personalScheduleElement 추가하고 싶은 고정 일정
     * @return 추가된 personal schedule document
     * @throws ConflictScheduleException 스케줄의 시간이 충돌
     * @throws TimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws EndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun addFixedPersonalScheduleElement(personalScheduleElement: PersonalScheduleElement): PersonalSchedule {
        isValidTimeForFixedSchedule(personalScheduleElement)

        val sortedIndex = getSortedIndex(
            personalScheduleElement = personalScheduleElement,
            scheduleEnum = ScheduleEnum.FIXED_PERSONAL_SCHEDULE
        )

        fixedSchedules.add(sortedIndex, personalScheduleElement)
        return this
    }

    /**
     * 유동 스케줄에 새로운 유동 일정을 추가
     * @param personalScheduleElement 추가하고 싶은 유동 일정
     * @return 추가된 personal schedule document
     * @throws ConflictScheduleException 스케줄의 시간이 충돌
     * @throws TimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws EndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun addFlexiblePersonalScheduleElement(personalScheduleElement: PersonalScheduleElement): PersonalSchedule {

        isValidTimeForFlexibleSchedule(personalScheduleElement)

        val sortedIndex = getSortedIndex(
            personalScheduleElement = personalScheduleElement,
            scheduleEnum = ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

        flexibleSchedules.add(sortedIndex, personalScheduleElement)

        return this
    }

    /**
     * 정렬된 스케줄 배열에 해당 스케줄의 인덱스를 탐색
     * @param personalScheduleElement 탐색하고 싶은 스케줄 요소
     * @param scheduleEnum 탐색하고 싶은 스케줄 배열
     * @return 삽입될 스케줄의 인덱스
     * @throws ConflictScheduleException 스케줄의 시간이 충돌
     */
    fun getSortedIndex(personalScheduleElement: PersonalScheduleElement, scheduleEnum: ScheduleEnum): Int {
        val schedules:MutableList<PersonalScheduleElement> = when(scheduleEnum){
            ScheduleEnum.FIXED_PERSONAL_SCHEDULE -> fixedSchedules
            ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE -> flexibleSchedules
        }

        val index = schedules.binarySearch(0){
            it.startTime.compareTo(personalScheduleElement.startTime)
        }

        if(index >= 0)
            throw ConflictScheduleException()

        val sortedIndex = (index + 1) * -1

        //앞의 종료 시간과 해당 스케줄의 시작 시간을 비교
        if(sortedIndex > 0)
            if(schedules[sortedIndex - 1].endTime >= personalScheduleElement.startTime)
                throw ConflictScheduleException()

        //뒤의 시작 시간과 해당 스케줄의 종료 시간을 비교
        if(sortedIndex < schedules.size)
            if(schedules[sortedIndex].startTime <= personalScheduleElement.endTime)
                throw ConflictScheduleException()

        return sortedIndex
    }

    /**
     * 유동 스케줄의 시간이 유효한지 확인
     * @param personalScheduleElement 확인할 요소
     * @return true
     * @throws TimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws EndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun isValidTimeForFlexibleSchedule(personalScheduleElement: PersonalScheduleElement): Boolean{
        return isStartTimeBefore(personalScheduleElement = personalScheduleElement) &&
                //00(년)_01(월)_01(일)_00(시)_00(분) ~ 99(년)_12(월)_31(일)_23(시)_59(분)
                isTimeRange(
                    personalScheduleElement = personalScheduleElement,
                    startTimeRange = "0001010000",
                    endTimeRange = "9912312359",)
    }

    /**
     * 고정 스케줄의 시간이 유효한지 확인
     * @param personalScheduleElement 확인할 요소
     * @return true
     * @throws TimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws EndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun isValidTimeForFixedSchedule(personalScheduleElement: PersonalScheduleElement): Boolean{
        return isStartTimeBefore(personalScheduleElement = personalScheduleElement) &&
                //1(요일)_00(시)_00(분) ~ 7(요일)_23(시)_59(분)
                isTimeRange(personalScheduleElement = personalScheduleElement,
                    startTimeRange = "10000",
                    endTimeRange = "72359")
    }

    /**
     * 해당 스케줄의 시간이 범위 내에 있는지 확인
     * @param personalScheduleElement 확인할 요소
     * @param startTimeRange 시작 범위
     * @param endTimeRange 종료 범위
     * @return true
     * @throws TimeIsNotRangeException 유효한 시간 범위가 아님
     */
    private fun isTimeRange(
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
    private fun isStartTimeBefore(personalScheduleElement: PersonalScheduleElement): Boolean{
        if(personalScheduleElement.startTime.length != personalScheduleElement.endTime.length ||
            personalScheduleElement.startTime > personalScheduleElement.endTime)
            throw EndTimeBeforeStartTimeException()
        return true
    }
}

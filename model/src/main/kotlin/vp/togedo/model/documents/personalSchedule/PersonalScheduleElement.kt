package vp.togedo.model.documents.personalSchedule

import org.bson.types.ObjectId
import vp.togedo.model.exception.personalSchedule.PersonalScheduleEndTimeBeforeStartTimeException
import vp.togedo.model.exception.personalSchedule.PersonalScheduleTimeIsNotRangeException

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

    /**
     * 해당 스케줄의 시간이 범위 내에 있는지 확인
     * @param startTimeRange 시작 범위
     * @param endTimeRange 종료 범위
     * @return true
     * @throws PersonalScheduleTimeIsNotRangeException 유효한 시간 범위가 아님
     */
    private fun isTimeRange(
        startTimeRange: String,
        endTimeRange: String): Boolean{
        if(startTime.length != startTimeRange.length ||
            endTime.length != endTimeRange.length ||
            startTime !in startTimeRange..endTimeRange ||
            endTime !in startTimeRange..endTimeRange){
            throw PersonalScheduleTimeIsNotRangeException()
        }
        return true
    }

    /**
     * 유동 스케줄의 시간이 유효한지 확인
     * @return true
     * @throws PersonalScheduleTimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws PersonalScheduleEndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun isValidTimeForFlexibleSchedule(): Boolean{
        return isStartTimeBefore() &&
                //00(년)_01(월)_01(일)_00(시)_00(분) ~ 99(년)_12(월)_31(일)_23(시)_59(분)
                isTimeRange(
                    startTimeRange = "0001010000",
                    endTimeRange = "9912312359",)
    }

    /**
     * 고정 스케줄의 시간이 유효한지 확인
     * @return true
     * @throws PersonalScheduleTimeIsNotRangeException 유효한 시간 범위가 아님
     * @throws PersonalScheduleEndTimeBeforeStartTimeException 종료 시간이 시작 시간보다 앞에 있음
     */
    fun isValidTimeForFixedSchedule(): Boolean{
        return isStartTimeBefore() &&
                //1(요일)_00(시)_00(분) ~ 7(요일)_23(시)_59(분)
                isTimeRange(
                    startTimeRange = "10000",
                    endTimeRange = "72359")
    }

}
package vp.togedo.model.documents.personalSchedule

import org.junit.jupiter.api.*
import vp.togedo.model.exception.personalSchedule.PersonalScheduleEndTimeBeforeStartTimeException
import vp.togedo.model.exception.personalSchedule.PersonalScheduleTimeIsNotRangeException
import java.util.*

class PersonalScheduleElementTest{

    @Nested
    inner class IsValidTimeForFixedSchedule{
        private lateinit var personalScheduleElement: PersonalScheduleElement

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 종료시간보다 앞에 있는 경우")
        fun startTimeBeforeEndTimeInFixedScheduleReturnSuccess() {
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isValidTimeForFixedSchedule()

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 종료시간과 같은 경우")
        fun startTimeEqualEndTimeInFixedScheduleReturnSuccess() {
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isValidTimeForFixedSchedule()

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 종료시간보다 뒤에 있는 경우")
        fun startTimeAfterEndTimeInFixedScheduleReturnException() {
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "10001",
                endTime = "10000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleEndTimeBeforeStartTimeException::class.java) {
                personalScheduleElement.isValidTimeForFixedSchedule()
            }
        }

        @Test
        @DisplayName("시작시간과 종료 시간의 길이가 다른 경우")
        fun startTimeAndEndTimeLengthNotEqualReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "1000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleEndTimeBeforeStartTimeException::class.java){
                personalScheduleElement.isValidTimeForFlexibleSchedule()
            }
        }

        @Test
        @DisplayName("고정 스케줄의 시간이 유효한 길이인 경우")
        fun startTimeInLengthRangeInFixedScheduleReturnSuccess(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isValidTimeForFixedSchedule()

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄의 시간이 유효하지 않은 길이인 경우")
        fun startTimeOutLengthRangeInFixedScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "1000",
                endTime = "1001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleTimeIsNotRangeException::class.java) {
                personalScheduleElement.isValidTimeForFixedSchedule()
            }
        }

        @Test
        @DisplayName("스케줄의 시작 시간이 유효한 범위가 아닌 경우")
        fun startTimeOitRangeInFixedScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "00000",
                endTime = "10010",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleTimeIsNotRangeException::class.java) {
                personalScheduleElement.isValidTimeForFixedSchedule()
            }
        }

        @Test
        @DisplayName("스케줄의 종료 시간이 유효한 범위가 아닌 경우")
        fun endTimeOitRangeInFixedScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "00000",
                endTime = "90010",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleTimeIsNotRangeException::class.java) {
                personalScheduleElement.isValidTimeForFixedSchedule()
            }
        }

    }

    @Nested
    inner class IsValidTimeForFlexibleSchedule {
        private lateinit var personalScheduleElement: PersonalScheduleElement


        @Test
        @DisplayName("유동 스케줄의 시작 시간이 종료시간과 같은 경우")
        fun startTimeEqualEndTimeInFlexibleScheduleReturnSuccess(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "2501060000",
                endTime = "2501060000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isValidTimeForFlexibleSchedule()

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("유동 스케줄의 시작 시간이 종료시간보다 뒤에 있는 경우")
        fun startTimeAfterEndTimeInFlexibleScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "2501060001",
                endTime = "2501060000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleEndTimeBeforeStartTimeException::class.java){
                personalScheduleElement.isValidTimeForFlexibleSchedule()
            }
        }

        @Test
        @DisplayName("시작시간과 종료 시간의 길이가 다른 경우")
        fun startTimeAndEndTimeLengthNotEqualReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "2501060000",
                endTime = "250106000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleEndTimeBeforeStartTimeException::class.java){
                personalScheduleElement.isValidTimeForFlexibleSchedule()
            }
        }

        @Test
        @DisplayName("유동 스케줄의 시간이 유효한 길이인 경우")
        fun startTimeInLengthRangeInFlexibleScheduleReturnSuccess(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "2501060000",
                endTime = "2501060001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isValidTimeForFlexibleSchedule()

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("유동 스케줄의 시간이 유효하지 않은 길이인 경우")
        fun startTimeOutLengthRangeInFlexibleScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "1000",
                endTime = "1001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleTimeIsNotRangeException::class.java) {
                personalScheduleElement.isValidTimeForFlexibleSchedule()
            }
        }

        @Test
        @DisplayName("스케줄의 시작 시간이 유효한 범위가 아닌 경우")
        fun startTimeOitRangeInFixedScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "0000000000",
                endTime = "0001010000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleTimeIsNotRangeException::class.java) {
                personalScheduleElement.isValidTimeForFlexibleSchedule()
            }
        }

        @Test
        @DisplayName("스케줄의 종료 시간이 유효한 범위가 아닌 경우")
        fun endTimeOitRangeInFixedScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "0001010000",
                endTime = "9999999999",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleTimeIsNotRangeException::class.java) {
                personalScheduleElement.isValidTimeForFlexibleSchedule()
            }
        }
    }
}
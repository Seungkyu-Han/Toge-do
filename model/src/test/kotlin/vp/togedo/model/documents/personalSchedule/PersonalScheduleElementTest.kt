package vp.togedo.model.documents.personalSchedule

import org.junit.jupiter.api.*
import vp.togedo.model.exception.personalSchedule.PersonalScheduleEndTimeBeforeStartTimeException
import java.util.*

class PersonalScheduleElementTest{

    @Nested
    inner class IsStartTimeBefore{
        private lateinit var personalScheduleElement: PersonalScheduleElement

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 종료시간보다 앞에 있는 경우")
        fun startTimeBeforeEndTimeInFixedScheduleReturnSuccess(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isStartTimeBefore()

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 종료시간과 같은 경우")
        fun startTimeEqualEndTimeInFixedScheduleReturnSuccess(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isStartTimeBefore()

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 종료시간보다 뒤에 있는 경우")
        fun startTimeAfterEndTimeInFixedScheduleReturnException(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "10001",
                endTime = "10000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(PersonalScheduleEndTimeBeforeStartTimeException::class.java){
                personalScheduleElement.isStartTimeBefore()
            }
        }

        @Test
        @DisplayName("유동 스케줄의 시작 시간이 종료시간보다 앞에 있는 경우")
        fun startTimeBeforeEndTimeInFlexibleScheduleReturnSuccess(){
            //given
            personalScheduleElement = PersonalScheduleElement(
                startTime = "2501060000",
                endTime = "2501060001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalScheduleElement.isStartTimeBefore()

            //then
            Assertions.assertTrue(result)
        }

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
            val result = personalScheduleElement.isStartTimeBefore()

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
                personalScheduleElement.isStartTimeBefore()
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
                personalScheduleElement.isStartTimeBefore()
            }
        }
    }
}
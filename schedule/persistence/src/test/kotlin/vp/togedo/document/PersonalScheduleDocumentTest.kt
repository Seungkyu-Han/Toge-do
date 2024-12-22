package vp.togedo.document

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException
import java.util.UUID

class PersonalScheduleDocumentTest{

    @Nested
    @DisplayName("시작시간이 종료시간보다 앞인지 확인하는 메서드 테스트")
    inner class  IsStartTimeBefore{

        private val personalSchedule = PersonalScheduleDocument(
            userId = ObjectId(),
        )

        @Test
        @DisplayName("고정 스케줄에 유효한 값이 입력된 경우")
        fun fixedScheduleStartTimeBeforeEndTimeReturnSuccess(){
            //given
            val fixedSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 10000,
                endTime = 10059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.isStartTimeBefore(fixedSchedule)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("유동 스케줄에 유효한 값이 입력된 경우")
        fun flexibleScheduleStartTimeBeforeEndTimeReturnSuccess(){
            //given
            val fixedSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_10_00,
                endTime = 24_12_22_10_59,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.isStartTimeBefore(fixedSchedule)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄에 시작시간과 종료시간이 같은 경우")
        fun fixedScheduleStartTimeEqualEndTimeReturnSuccess(){
            //given
            val fixedSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 10000,
                endTime = 10000,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.isStartTimeBefore(fixedSchedule)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("유동 스케줄에 시작시간과 종료시간이 같은 경우")
        fun flexibleScheduleStartTimeEqualEndTimeReturnSuccess(){
            //given
            val fixedSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_10_00,
                endTime = 24_12_22_10_00,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.isStartTimeBefore(fixedSchedule)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄에 종료시간이 시작시간보다 앞인 경우")
        fun fixedScheduleStartTimeAfterEndTimeReturnSuccess(){
            //given
            val fixedSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 10059,
                endTime = 10000,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )


            //when && then
            Assertions.assertThrows(EndTimeBeforeStartTimeException::class.java){
                personalSchedule.isStartTimeBefore(fixedSchedule)
            }
        }

        @Test
        @DisplayName("유동 스케줄에 시작시간과 종료시간이 같은 경우")
        fun flexibleScheduleStartTimeAfterEndTimeReturnSuccess(){
            //given
            val fixedSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_10_59,
                endTime = 24_12_22_10_00,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(EndTimeBeforeStartTimeException::class.java){
                personalSchedule.isStartTimeBefore(fixedSchedule)
            }
        }
    }

    @Nested
    @DisplayName("해당 스케줄에 시간 범위를 체크하는 메서드")
    inner class IsValidTime{

        private val fixedScheduleStartTime = 1_00_00L
        private val fixedScheduleEndTime = 7_23_59L

        private val flexibleScheduleStartTime = 10_01_01_00_00L
        private val flexibleScheduleEndTime = 99_12_31_23_59L

        private val personalSchedule = PersonalScheduleDocument(
            userId = ObjectId.get()
        )

        @Test
        @DisplayName("고정 스케줄이 시간 범위 내에 있는 경우")
        fun fixedScheduleTimeIsValidTimeRangeReturnSuccess(){
            //given
            val time = 1_10_00L

            //when
            val result = personalSchedule.isValidTime(time, fixedScheduleStartTime, fixedScheduleEndTime)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("유동 스케줄이 시간 범위 내에 있는 경우")
        fun flexibleScheduleTimeIsValidTimeRangeReturnSuccess(){
            //given
            val time = 24_12_22_10_59

            //when
            val result = personalSchedule.isValidTime(time, flexibleScheduleStartTime, flexibleScheduleEndTime)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("고정 스케줄이 시간 범위 전에 있는 경우")
        fun fixedScheduleTimeBeforeTimeRangeReturnException(){
            //given
            val time = 10_00L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, fixedScheduleStartTime, fixedScheduleEndTime)
            }
        }

        @Test
        @DisplayName("유동 스케줄이 시간 범위 전에 있는 경우")
        fun flexibleScheduleTimeBeforeTimeRangeReturnException(){
            //given
            val time = 8_12_22_10_59L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, flexibleScheduleStartTime, flexibleScheduleEndTime)
            }
        }

        @Test
        @DisplayName("고정 스케줄이 시간 범위 후에 있는 경우")
        fun fixedScheduleTimeAfterTimeRangeReturnException(){
            //given
            val time = 8_23_00L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, fixedScheduleStartTime, fixedScheduleEndTime)
            }
        }

        @Test
        @DisplayName("유동 스케줄이 시간 범위 후에 있는 경우")
        fun flexibleScheduleTimeAfterTimeRangeReturnException(){
            //given
            val time = 100_12_22_10_59L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, flexibleScheduleStartTime, flexibleScheduleEndTime)
            }
        }

        @Test
        @DisplayName("고정 스케줄이 시 범위 후에 있는 경우")
        fun fixedScheduleTimeAfterHourRangeReturnException(){
            //given
            val time = 6_24_00L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, fixedScheduleStartTime, fixedScheduleEndTime)
            }
        }

        @Test
        @DisplayName("유동 스케줄이 시 범위 후에 있는 경우")
        fun flexibleScheduleTimeAfterHourRangeReturnException(){
            //given
            val time = 100_12_22_24_59L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, flexibleScheduleStartTime, flexibleScheduleEndTime)
            }
        }


        @Test
        @DisplayName("고정 스케줄이 분 범위 후에 있는 경우")
        fun fixedScheduleTimeAfterMinuteRangeReturnException(){
            //given
            val time = 6_23_70L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, fixedScheduleStartTime, fixedScheduleEndTime)
            }
        }

        @Test
        @DisplayName("유동 스케줄이 분 범위 후에 있는 경우")
        fun flexibleScheduleTimeAfterMinuteRangeReturnException(){
            //given
            val time = 100_12_22_23_70L

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                personalSchedule.isValidTime(time, flexibleScheduleStartTime, flexibleScheduleEndTime)
            }
        }


    }

}
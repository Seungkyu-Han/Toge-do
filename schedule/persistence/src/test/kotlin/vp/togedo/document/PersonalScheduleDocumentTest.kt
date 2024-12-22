package vp.togedo.document

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
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
}
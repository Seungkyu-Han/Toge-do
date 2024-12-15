package vp.togedo.document

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.InvalidTimeException
import java.util.*

class FixedPersonalScheduleDocumentTest{


    @Nested
    inner class IsValidTime{
        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = ObjectId.get(),
            schedules = mutableListOf()
        )

        @Test
        @DisplayName("시작시간, 종료시간이 모두 범위 내")
        fun startTimeAndEndTimeValidReturnSuccess(){
            //given
            val startTime = 11111
            val endTime = 22222
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = startTime,
                endTime = endTime,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = fixedPersonalScheduleDocument.isValidTime(schedule)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("시작 시간이 시간 범위 전")
        fun startTimeBeforeRangeReturnException(){
            //given
            val startTime = 1111
            val endTime = 22222
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = startTime,
                endTime = endTime,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java) { fixedPersonalScheduleDocument.isValidTime(schedule) }
        }

        @Test
        @DisplayName("종료 시간이 시간 범위 뒤")
        fun endTimeAfterRangeReturnException(){
            //given
            val startTime = 11111
            val endTime = 73333
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = startTime,
                endTime = endTime,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java) { fixedPersonalScheduleDocument.isValidTime(schedule) }
        }
    }

    @Nested
    inner class ValidTimeCheck{

        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = ObjectId.get(),
            schedules = mutableListOf()
        )
        @Test
        @DisplayName("시간이 범위 내")
        fun validTimeReturnSuccess(){
            //given
            val time = 11111

            //when
            val result = fixedPersonalScheduleDocument.validTimeCheck(time)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("시간이 주 범위 전")
        fun timeBeforeWeekReturnException(){
            //given
            val time = 1111

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                fixedPersonalScheduleDocument.validTimeCheck(time)
            }
        }

        @Test
        @DisplayName("시간이 주 범위 후")
        fun timeAfterWeekReturnException(){
            //given
            val time = 81111

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                fixedPersonalScheduleDocument.validTimeCheck(time)
            }
        }

        @Test
        @DisplayName("시간이 hour 범위 후")
        fun timeAfterHourReturnException(){
            //given
            val time = 12411

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                fixedPersonalScheduleDocument.validTimeCheck(time)
            }
        }

        @Test
        @DisplayName("시간이 minute 범위 후")
        fun timeAfterMinuteReturnException(){
            //given
            val time = 12360

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java){
                fixedPersonalScheduleDocument.validTimeCheck(time)
            }
        }
    }

    @Nested
    inner class IsConflictTime{
        private lateinit var fixedPersonalScheduleDocument: FixedPersonalScheduleDocument

        @BeforeEach
        fun init(){
            fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
                id = ObjectId.get(),
                userId = ObjectId.get(),
                schedules = mutableListOf()
            )
        }

        @Test
        @DisplayName("스케줄이 비어있는 경우")
        fun isEmptyScheduleReturn0(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11300,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(schedule)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("충돌하지 않는 스케줄이 뒤에 하나만 존재하는 경우")
        fun scheduleAfterOneElementReturn0(){
            //given
            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.schedules.add(afterSchedule)

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(schedule)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("충돌하지 않는 스케줄이 앞에 하나만 존재하는 경우")
        fun scheduleBeforeOneElementReturn1(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.schedules.add(beforeSchedule)

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(schedule)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("충돌하지 않는 스케줄이 앞뒤에 하나씩 존재하는 경우")
        fun scheduleBetweenNonConflictReturn1(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11300,
                endTime = 11359,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.schedules.add(beforeSchedule)
            fixedPersonalScheduleDocument.schedules.add(afterSchedule)

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(schedule)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("앞의 종료 시간이 아직 끝나지 않은 경우")
        fun conflictWithBeforeEndTimeReturnException(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11200,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(beforeSchedule)

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                fixedPersonalScheduleDocument.isConflictTime(schedule)
            }
        }

        @Test
        @DisplayName("뒤의 시작시간과 충돌하는 경우")
        fun conflictWithAfterStartTimeReturnException(){
            //given
            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11300,
                endTime = 11359,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11300,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(afterSchedule)

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                fixedPersonalScheduleDocument.isConflictTime(schedule)
            }
        }

        @Test
        @DisplayName("시작 시간이 동일한 스케줄이 존재하는 경우")
        fun conflictWithEqualsStartTimeReturnException(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(beforeSchedule)

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                fixedPersonalScheduleDocument.isConflictTime(schedule)
            }
        }
    }

    @Nested
    inner class AddSchedule{
        private lateinit var fixedPersonalScheduleDocument: FixedPersonalScheduleDocument

        @BeforeEach
        fun init(){
            fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
                id = ObjectId.get(),
                userId = ObjectId.get(),
                schedules = mutableListOf()
            )
        }

        @Test
        @DisplayName("빈 리스트에 스케줄 삽입")
        fun addScheduleInEmptyScheduleListReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(schedule))
                .expectNextMatches {
                    it.schedules.size == 1
                }.verifyComplete()
        }

        @Test
        @DisplayName("리스트 사이에 스케줄 삽입")
        fun addScheduleBetweenScheduleListReturnSuccess(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(beforeSchedule)
            fixedPersonalScheduleDocument.schedules.add(afterSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(schedule))
                .expectNextMatches {
                    it.schedules.size == 3 &&
                            it.schedules[0].id == beforeSchedule.id &&
                            it.schedules[1].id == schedule.id &&
                            it.schedules[2].id == afterSchedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("리스트 앞에 스케줄 삽입")
        fun addScheduleBeforeScheduleListReturnSuccess(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(beforeSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(schedule))
                .expectNextMatches {
                    it.schedules.size == 2 &&
                            it.schedules[0].id == beforeSchedule.id &&
                            it.schedules[1].id == schedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("리스트 뒤에 스케줄 삽입")
        fun addScheduleAfterScheduleListReturnSuccess(){
            //given
            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(afterSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(schedule))
                .expectNextMatches {
                    it.schedules.size == 2 &&
                            it.schedules[0].id == schedule.id &&
                            it.schedules[1].id == afterSchedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("앞 스케줄과 충돌")
        fun addScheduleConflictBeforeScheduleReturnException(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11100,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(beforeSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(schedule))
                .expectErrorMatches {
                    it is ConflictScheduleException
                }.verify()

        }

        @Test
        @DisplayName("뒤 스케줄과 충돌")
        fun addScheduleConflictAfterScheduleReturnException(){
            //given
            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11200,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.schedules.add(afterSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(schedule))
                .expectErrorMatches {
                    it is ConflictScheduleException
                }.verify()
        }
    }
}
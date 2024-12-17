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
import vp.togedo.util.exception.ScheduleNotFoundException
import java.util.*

class FixedPersonalScheduleDocumentTest{


    @Nested
    inner class IsValidTime{
        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = ObjectId.get(),
            fixedSchedules = mutableListOf()
        )

        @Test
        @DisplayName("시작시간, 종료시간이 모두 범위 내")
        fun startTimeAndEndTimeValidReturnSuccess(){
            //given
            val startTime = 11111
            val endTime = 22222
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = startTime,
                endTime = endTime,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = fixedPersonalScheduleDocument.isValidTime(fixedSchedule)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("시작 시간이 시간 범위 전")
        fun startTimeBeforeRangeReturnException(){
            //given
            val startTime = 1111
            val endTime = 22222
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = startTime,
                endTime = endTime,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java) { fixedPersonalScheduleDocument.isValidTime(fixedSchedule) }
        }

        @Test
        @DisplayName("종료 시간이 시간 범위 뒤")
        fun endTimeAfterRangeReturnException(){
            //given
            val startTime = 11111
            val endTime = 73333
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = startTime,
                endTime = endTime,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(InvalidTimeException::class.java) { fixedPersonalScheduleDocument.isValidTime(fixedSchedule) }
        }
    }

    @Nested
    inner class ValidTimeCheck{

        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = ObjectId.get(),
            fixedSchedules = mutableListOf()
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
                fixedSchedules = mutableListOf()
            )
        }

        @Test
        @DisplayName("스케줄이 비어있는 경우")
        fun isEmptyScheduleReturn0(){
            //given
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11300,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(fixedSchedule)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("충돌하지 않는 스케줄이 뒤에 하나만 존재하는 경우")
        fun scheduleAfterOneElementReturn0(){
            //given
            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(fixedSchedule)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("충돌하지 않는 스케줄이 앞에 하나만 존재하는 경우")
        fun scheduleBeforeOneElementReturn1(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(fixedSchedule)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("충돌하지 않는 스케줄이 앞뒤에 하나씩 존재하는 경우")
        fun scheduleBetweenNonConflictReturn1(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11300,
                endTime = 11359,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)
            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)

            //when
            val result = fixedPersonalScheduleDocument.isConflictTime(fixedSchedule)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("앞의 종료 시간이 아직 끝나지 않은 경우")
        fun conflictWithBeforeEndTimeReturnException(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11200,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                fixedPersonalScheduleDocument.isConflictTime(fixedSchedule)
            }
        }

        @Test
        @DisplayName("뒤의 시작시간과 충돌하는 경우")
        fun conflictWithAfterStartTimeReturnException(){
            //given
            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11300,
                endTime = 11359,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11300,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                fixedPersonalScheduleDocument.isConflictTime(fixedSchedule)
            }
        }

        @Test
        @DisplayName("시작 시간이 동일한 스케줄이 존재하는 경우")
        fun conflictWithEqualsStartTimeReturnException(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                fixedPersonalScheduleDocument.isConflictTime(fixedSchedule)
            }
        }
    }

    @Nested
    inner class AddFixedSchedule{
        private lateinit var fixedPersonalScheduleDocument: FixedPersonalScheduleDocument

        @BeforeEach
        fun init(){
            fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
                id = ObjectId.get(),
                userId = ObjectId.get(),
                fixedSchedules = mutableListOf()
            )
        }

        @Test
        @DisplayName("빈 리스트에 스케줄 삽입")
        fun addScheduleInEmptyScheduleListReturnSuccess(){
            //given
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(fixedSchedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 1
                }.verifyComplete()
        }

        @Test
        @DisplayName("리스트 사이에 스케줄 삽입")
        fun addScheduleBetweenScheduleListReturnSuccess(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)
            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(fixedSchedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 3 &&
                            it.fixedSchedules[0].id == beforeFixedSchedule.id &&
                            it.fixedSchedules[1].id == fixedSchedule.id &&
                            it.fixedSchedules[2].id == afterFixedSchedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("리스트 앞에 스케줄 삽입")
        fun addScheduleBeforeScheduleListReturnSuccess(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(fixedSchedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 2 &&
                            it.fixedSchedules[0].id == beforeFixedSchedule.id &&
                            it.fixedSchedules[1].id == fixedSchedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("리스트 뒤에 스케줄 삽입")
        fun addScheduleAfterScheduleListReturnSuccess(){
            //given
            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(fixedSchedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 2 &&
                            it.fixedSchedules[0].id == fixedSchedule.id &&
                            it.fixedSchedules[1].id == afterFixedSchedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("앞 스케줄과 충돌")
        fun addScheduleConflictBeforeScheduleReturnException(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11100,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(fixedSchedule))
                .expectErrorMatches {
                    it is ConflictScheduleException
                }.verify()

        }

        @Test
        @DisplayName("뒤 스케줄과 충돌")
        fun addScheduleConflictAfterScheduleReturnException(){
            //given
            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11200,
                endTime = 11259,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11200,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)


            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.addSchedule(fixedSchedule))
                .expectErrorMatches {
                    it is ConflictScheduleException
                }.verify()
        }
    }

    @Nested
    inner class DeleteFixedScheduleById{

        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = ObjectId.get(),
            fixedSchedules = mutableListOf()
        )

        @Test
        @DisplayName("하나만 존재하는 리스트에서 존재하는 스케줄을 삭제")
        fun deleteExistScheduleInOneElementScheduleListReturnSuccess(){
            //given
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(fixedSchedule)

            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.deleteScheduleById(fixedSchedule.id))
                .verifyComplete()
        }

        @Test
        @DisplayName("2개 이상 존재하는 리스트에서 존재하는 스케줄을 삭제")
        fun deleteExistScheduleMoreThenTwoElementScheduleListReturnSuccess(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11000,
                endTime = 11059,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)
            fixedPersonalScheduleDocument.fixedSchedules.add(fixedSchedule)

            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.deleteScheduleById(fixedSchedule.id))
                .verifyComplete()
        }

        @Test
        @DisplayName("존재하지 않는 스케줄을 삭제 시도")
        fun deleteNotExistScheduleReturnException(){
            //given
            val scheduleId = ObjectId.get()

            //when
            StepVerifier.create(fixedPersonalScheduleDocument.deleteScheduleById(scheduleId))
                .expectErrorMatches {
                    it is ScheduleNotFoundException
                }.verify()
        }
    }

    @Nested
    inner class ModifyFixedScheduleById{
        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = ObjectId.get(),
            fixedSchedules = mutableListOf()
        )

        @Test
        @DisplayName("존재하는 스케줄을 수정")
        fun modifyScheduleToExistScheduleReturnSuccess(){
            //given
            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.fixedSchedules.add(fixedSchedule)

            val modifiedStartTime = 11200
            val modifiedEndTime = 11259
            val modifiedTitle = UUID.randomUUID().toString()
            val modifiedColor = UUID.randomUUID().toString()

            //when && then

            StepVerifier.create(fixedPersonalScheduleDocument.modifyScheduleById(
                id = fixedSchedule.id,
                startTime = modifiedStartTime,
                endTime = modifiedEndTime,
                title = modifiedTitle,
                color = modifiedColor
            )).expectNextMatches {
                it.fixedSchedules.size == 1 &&
                        it.fixedSchedules[0].id == fixedSchedule.id &&
                        it.fixedSchedules[0].startTime == modifiedStartTime &&
                        it.fixedSchedules[0].endTime == modifiedEndTime &&
                        it.fixedSchedules[0].title == modifiedTitle &&
                        it.fixedSchedules[0].color == modifiedColor
            }.verifyComplete()

            Assertions.assertTrue{
                fixedPersonalScheduleDocument.fixedSchedules.size == 1 &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].id == fixedSchedule.id &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].startTime == modifiedStartTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].endTime == modifiedEndTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].title == modifiedTitle &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].color == modifiedColor
            }
        }


        @Test
        @DisplayName("사이에 존재하는 스케줄을 수정")
        fun modifyScheduleToSandwichScheduleReturnSuccess(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 31100,
                endTime = 31159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 21100,
                endTime = 21159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)
            fixedPersonalScheduleDocument.fixedSchedules.add(fixedSchedule)
            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)

            val modifiedStartTime = 41200
            val modifiedEndTime = 41259
            val modifiedTitle = UUID.randomUUID().toString()
            val modifiedColor = UUID.randomUUID().toString()

            //when && then

            println(fixedPersonalScheduleDocument)

            StepVerifier.create(fixedPersonalScheduleDocument.modifyScheduleById(
                id = fixedSchedule.id,
                startTime = modifiedStartTime,
                endTime = modifiedEndTime,
                title = modifiedTitle,
                color = modifiedColor
            )).expectNextMatches {
                it.fixedSchedules.size == 3 &&

                        it.fixedSchedules[0].id == beforeFixedSchedule.id &&
                        it.fixedSchedules[0].startTime == beforeFixedSchedule.startTime &&
                        it.fixedSchedules[0].endTime == beforeFixedSchedule.endTime &&
                        it.fixedSchedules[0].title == beforeFixedSchedule.title &&
                        it.fixedSchedules[0].color == beforeFixedSchedule.color




                        it.fixedSchedules[2].id == fixedSchedule.id &&
                        it.fixedSchedules[2].startTime == modifiedStartTime &&
                        it.fixedSchedules[2].endTime == modifiedEndTime &&
                        it.fixedSchedules[2].title == modifiedTitle &&
                        it.fixedSchedules[2].color == modifiedColor
            }.verifyComplete()
        }

        @Test
        @DisplayName("존재하지 않는 스케줄을 수정")
        fun modifyScheduleToNotExistScheduleReturnException(){
            //given
            val scheduleId = ObjectId.get()

            //when && then
            StepVerifier.create(fixedPersonalScheduleDocument.modifyScheduleById(
                id = scheduleId,
                startTime = 11000,
                endTime = 11159,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()))
                .expectErrorMatches {
                    it is ScheduleNotFoundException
                }.verify()
        }

        @Test
        @DisplayName("충돌하는 시간으로 수정")
        fun modifyScheduleToConflictScheduleReturnException(){
            //given
            val beforeFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 11100,
                endTime = 11159,
                title = "before schedule",
                color = UUID.randomUUID().toString()
            )

            val afterFixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 31100,
                endTime = 31159,
                title = "after schedule",
                color = UUID.randomUUID().toString()
            )

            val fixedSchedule = FixedSchedule(
                id = ObjectId.get(),
                startTime = 21100,
                endTime = 21159,
                title = "original schedule",
                color = UUID.randomUUID().toString()
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(beforeFixedSchedule)
            fixedPersonalScheduleDocument.fixedSchedules.add(fixedSchedule)
            fixedPersonalScheduleDocument.fixedSchedules.add(afterFixedSchedule)

            //when
            StepVerifier.create(fixedPersonalScheduleDocument.modifyScheduleById(
                id = fixedSchedule.id,
                startTime = 11100,
                endTime = 11159,
                title = "new schedule",
                color = UUID.randomUUID().toString()
            )).expectErrorMatches {
                it is ConflictScheduleException
            }.verify()

            //then
            Assertions.assertEquals(3, fixedPersonalScheduleDocument.fixedSchedules.size)

            Assertions.assertTrue{
                fixedPersonalScheduleDocument.fixedSchedules[0].id == beforeFixedSchedule.id &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].startTime == beforeFixedSchedule.startTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].endTime == beforeFixedSchedule.endTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].title == beforeFixedSchedule.title &&
                        fixedPersonalScheduleDocument.fixedSchedules[0].color == beforeFixedSchedule.color
            }

            Assertions.assertTrue{
                fixedPersonalScheduleDocument.fixedSchedules[1].id == fixedSchedule.id &&
                        fixedPersonalScheduleDocument.fixedSchedules[1].startTime == fixedSchedule.startTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[1].endTime == fixedSchedule.endTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[1].title == fixedSchedule.title &&
                        fixedPersonalScheduleDocument.fixedSchedules[1].color == fixedSchedule.color
            }

            Assertions.assertTrue{
                fixedPersonalScheduleDocument.fixedSchedules[2].id == afterFixedSchedule.id &&
                        fixedPersonalScheduleDocument.fixedSchedules[2].startTime == afterFixedSchedule.startTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[2].endTime == afterFixedSchedule.endTime &&
                        fixedPersonalScheduleDocument.fixedSchedules[2].title == afterFixedSchedule.title &&
                        fixedPersonalScheduleDocument.fixedSchedules[2].color == afterFixedSchedule.color
            }
        }
    }
}
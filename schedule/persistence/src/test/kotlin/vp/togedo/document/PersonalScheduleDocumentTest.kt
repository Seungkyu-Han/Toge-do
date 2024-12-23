package vp.togedo.document

import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import vp.togedo.enums.ScheduleEnum
import vp.togedo.util.exception.ConflictScheduleException
import vp.togedo.util.exception.EndTimeBeforeStartTimeException
import vp.togedo.util.exception.InvalidTimeException
import vp.togedo.util.exception.ScheduleNotFoundException
import java.util.UUID

class PersonalScheduleDocumentTest{

    private val fixedScheduleStartTime = 1_00_00L
    private val fixedScheduleEndTime = 7_23_59L

    private val flexibleScheduleStartTime = 10_01_01_00_00L
    private val flexibleScheduleEndTime = 99_12_31_23_59L

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

    @Nested
    inner class CheckScheduleValidTime{

        private val personalSchedule = PersonalScheduleDocument(
            userId = ObjectId.get()
        )

        @Test
        @DisplayName("고정 스케줄이 모두 유효한 시간인 경우")
        fun fixedScheduleValidTimeReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000L,
                endTime = 11059L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.checkScheduleValidTime(schedule, fixedScheduleStartTime, fixedScheduleEndTime)

            //then
            Assertions.assertTrue(result)
        }

        @Test
        @DisplayName("유동 스케줄이 모두 유효한 시간인 경우")
        fun flexibleScheduleValidTimeReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.checkScheduleValidTime(schedule, flexibleScheduleStartTime, flexibleScheduleEndTime)

            //then
            Assertions.assertTrue(result)
        }
    }

    @Nested
    inner class GetInsertedIndex{

        private lateinit var personalSchedule: PersonalScheduleDocument

        @BeforeEach
        fun init(){
            personalSchedule = PersonalScheduleDocument(
                userId = ObjectId.get()
            )
        }

        @Test
        @DisplayName("빈 고정 스케줄 리스트에서 인덱스 탐색")
        fun fixedScheduleInsertIndexToEmptyListReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000L,
                endTime = 11059L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("빈 유동 스케줄 리스트에서 인덱스 탐색")
        fun flexibleScheduleInsertIndexToEmptyListReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("앞에 하나의 고정 스케줄만 있는 경우에 인덱스 탐색")
        fun fixedScheduleInsertIndexToBeforeOneScheduleReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 11000L,
                    endTime = 11059L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 21000L,
                endTime = 21059L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("앞에 하나의 유동 스케줄만 있는 경우에 인덱스 탐색")
        fun flexibleScheduleInsertIndexToBeforeOneScheduleReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_21_22_00L,
                    endTime = 24_12_21_22_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("뒤에 하나의 고정 스케줄만 있는 경우에 인덱스 탐색")
        fun fixedScheduleInsertIndexToAfterOneScheduleReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 21000L,
                    endTime = 21059L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000L,
                endTime = 11059L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("뒤에 하나의 유동 스케줄만 있는 경우에 인덱스 탐색")
        fun flexibleScheduleInsertIndexToAfterOneScheduleReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_23_22_00L,
                    endTime = 24_12_23_22_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("사이에 하나씩 고정 스케줄만 있는 경우에 인덱스 탐색")
        fun fixedScheduleInsertIndexBetweenOneScheduleReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 11000L,
                    endTime = 11059L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 31000L,
                    endTime = 31059L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 21000L,
                endTime = 21059L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("사이에 하나씩 유동 스케줄만 있는 경우에 인덱스 탐색")
        fun flexibleScheduleInsertIndexToBetweenOneScheduleReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_21_22_00L,
                    endTime = 24_12_21_22_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_23_22_00L,
                    endTime = 24_12_23_22_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("앞에 24개, 뒤에 24개의 고정 스케줄이 있는 경우 인덱스 탐색")
        fun fixedScheduleInsertIndexBetweenManyScheduleReturnSuccess(){
            //given
            for (i in 0..48){
                if (i == 24)
                    continue

                personalSchedule.fixedSchedules.add(
                    Schedule(
                        id = ObjectId.get(),
                        startTime = 11000L + i,
                        endTime = 11000L + i,
                        title = UUID.randomUUID().toString(),
                        color = UUID.randomUUID().toString()
                    )
                )

            }

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11024L,
                endTime = 11024L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(24, result)
        }

        @Test
        @DisplayName("앞에 24개, 뒤에 24개의 유동 스케줄이 있는 경우 인덱스 탐색")
        fun flexibleScheduleInsertIndexBetweenManyScheduleReturnSuccess(){
            //given
            for (i in 0..48){
                if (i == 24)
                    continue

                personalSchedule.flexibleSchedules.add(
                    Schedule(
                        id = ObjectId.get(),
                        startTime = 24_12_22_10_00L + i,
                        endTime = 24_12_22_10_00L + i,
                        title = UUID.randomUUID().toString(),
                        color = UUID.randomUUID().toString()
                    )
                )

            }

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_10_24L,
                endTime = 24_12_22_10_24L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            val result = personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)

            //then
            Assertions.assertEquals(24, result)
        }

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 겹치는 경우에 인덱스 탐색")
        fun fixedScheduleInsertIndexConflictStartTimeReturnException(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 11000L,
                    endTime = 11059L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000L,
                endTime = 11159L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)
            }
        }

        @Test
        @DisplayName("유동 스케줄의 시작 시간이 겹치는 경우에 인덱스 탐색")
        fun flexibleScheduleInsertIndexConflictStartTimeReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_21_22_00L,
                    endTime = 24_12_21_22_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_21_22_00L,
                endTime = 24_12_21_23_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)
            }
        }

        @Test
        @DisplayName("고정 스케줄의 시작 시간이 전 스케줄의 종료시간과 충돌 하는 경우에 인덱스 탐색")
        fun fixedScheduleInsertIndexConflictStartTimeWithBeforeEndTimeReturnException(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 11000L,
                    endTime = 11100L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11100L,
                endTime = 11159L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)
            }
        }

        @Test
        @DisplayName("유동 스케줄의 시작 시간이 전 스케줄의 종료시간과 충돌 하는 경우에 인덱스 탐색")
        fun flexibleScheduleInsertIndexConflictStartTimeWithBeforeEndTimeReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_21_22_00L,
                    endTime = 24_12_21_23_00L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_21_23_00L,
                endTime = 24_12_21_23_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)
            }
        }

        @Test
        @DisplayName("고정 스케줄의 종료 시간이 전 스케줄의 시작 시간과 충돌 하는 경우에 인덱스 탐색")
        fun fixedScheduleInsertIndexConflictEndTimeWithAfterStartTimeReturnException(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 11100L,
                    endTime = 11159L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 11000L,
                endTime = 11100L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FIXED_PERSONAL_SCHEDULE)
            }
        }

        @Test
        @DisplayName("유동 스케줄의 종료 시간이 전 스케줄의 시작 시간과 충돌 하는 경우에 인덱스 탐색")
        fun flexibleScheduleInsertIndexConflictEndTimeWithAfterStartTimeReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_21_22_00L,
                    endTime = 24_12_21_22_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_21_21_00L,
                endTime = 24_12_21_22_00L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            Assertions.assertThrows(ConflictScheduleException::class.java){
                personalSchedule.getInsertedIndex(schedule, ScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE)
            }
        }
    }

    @Nested
    inner class AddFixedSchedule{
        private lateinit var personalSchedule: PersonalScheduleDocument

        @BeforeEach
        fun init(){
            personalSchedule = PersonalScheduleDocument(
                userId = ObjectId.get()
            )
        }

        @Test
        @DisplayName("빈 고정 스케줄 리스트에 삽입")
        fun addFixedScheduleToEmptyListReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_11_00L,
                endTime = 1_11_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFixedSchedule(schedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 1 &&
                            it.fixedSchedules[0].id == schedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("앞에 하나의 요소가 있는 고정 스케줄 리스트에 삽입")
        fun addFixedScheduleToBeforeOneElementReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 1_10_00L,
                    endTime = 1_10_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_11_00L,
                endTime = 1_11_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFixedSchedule(schedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 2 &&
                            it.fixedSchedules[1].id == schedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("뒤에 하나의 요소가 있는 고정 스케줄 리스트에 삽입")
        fun addFixedScheduleToAfterOneElementReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 1_12_00L,
                    endTime = 1_12_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_11_00L,
                endTime = 1_11_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFixedSchedule(schedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 2 &&
                            it.fixedSchedules[0].id == schedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("사이에 하나씩의 요소가 있는 고정 스케줄 리스트에 삽입")
        fun addFixedScheduleToBetweenOneElementReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 1_10_00L,
                    endTime = 1_10_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            personalSchedule.fixedSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 1_12_00L,
                    endTime = 1_12_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_11_00L,
                endTime = 1_11_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFixedSchedule(schedule))
                .expectNextMatches {
                    it.fixedSchedules.size == 3 &&
                            it.fixedSchedules[1].id == schedule.id
                }.verifyComplete()
        }
    }

    @Nested
    inner class AddFlexibleSchedule{
        private lateinit var personalSchedule: PersonalScheduleDocument

        @BeforeEach
        fun init(){
            personalSchedule = PersonalScheduleDocument(
                userId = ObjectId.get()
            )
        }

        @Test
        @DisplayName("빈 유동 스케줄 리스트에 삽입")
        fun addFlexibleScheduleToEmptyListReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFlexibleSchedule(schedule))
                .expectNextMatches {
                    it.flexibleSchedules.size == 1 &&
                            it.flexibleSchedules[0].id == schedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("앞에 하나의 요소가 있는 유동 스케줄 리스트에 삽입")
        fun addFlexibleScheduleToBeforeOneElementReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_22_22_00L,
                    endTime = 24_12_22_22_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_23_00L,
                endTime = 24_12_22_23_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFlexibleSchedule(schedule))
                .expectNextMatches {
                    it.flexibleSchedules.size == 2 &&
                            it.flexibleSchedules[1].id == schedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("뒤에 하나의 요소가 있는 유동 스케줄 리스트에 삽입")
        fun addFlexibleScheduleToAfterOneElementReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_22_23_00L,
                    endTime = 24_12_22_23_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFlexibleSchedule(schedule))
                .expectNextMatches {
                    it.flexibleSchedules.size == 2 &&
                            it.flexibleSchedules[0].id == schedule.id
                }.verifyComplete()
        }

        @Test
        @DisplayName("사이에 하나씩의 요소가 있는 고정 스케줄 리스트에 삽입")
        fun addFixedScheduleToBetweenOneElementReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_22_21_00L,
                    endTime = 24_12_22_21_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            personalSchedule.flexibleSchedules.add(
                Schedule(
                    id = ObjectId.get(),
                    startTime = 24_12_22_23_00L,
                    endTime = 24_12_22_23_59L,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 24_12_22_22_00L,
                endTime = 24_12_22_22_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when && then
            StepVerifier.create(personalSchedule.addFlexibleSchedule(schedule))
                .expectNextMatches {
                    it.flexibleSchedules.size == 3 &&
                            it.flexibleSchedules[1].id == schedule.id
                }.verifyComplete()
        }
    }

    @Nested
    inner class DeleteFixedScheduleById{

        private lateinit var personalSchedule: PersonalScheduleDocument

        @BeforeEach
        fun init(){
            personalSchedule = PersonalScheduleDocument(
                userId = ObjectId.get()
            )
        }

        @Test
        @DisplayName("하나의 요소가 있는 스케줄에서 고정 스케줄을 삭제")
        fun deleteFixedScheduleFromOneElementScheduleReturnSuccess(){
            //given
            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_11_00L,
                endTime = 1_11_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            personalSchedule.fixedSchedules.add(schedule)

            //when && then
            StepVerifier.create(personalSchedule.deleteFixedScheduleById(schedule.id))
                .expectNextCount(1)
                .verifyComplete()

            Assertions.assertTrue{
                personalSchedule.flexibleSchedules.size == 0
            }
        }

        @Test
        @DisplayName("빈 스케줄에서 고정 스케줄을 삭제")
        fun deleteFixedScheduleFromEmptyScheduleReturnException(){
            //given

            //when && then
            StepVerifier.create(personalSchedule.deleteFixedScheduleById(ObjectId.get()))
                .expectErrorMatches {
                    it is ScheduleNotFoundException
                }.verify()

            Assertions.assertTrue{
                personalSchedule.flexibleSchedules.size == 0
            }
        }

        @Test
        @DisplayName("여러개의 요소가 있는 스케줄에서 고정 스케줄을 삭제")
        fun deleteFixedScheduleFromManyElementScheduleReturnSuccess(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_10_00L,
                endTime = 1_10_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_11_00L,
                endTime = 1_11_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_12_00L,
                endTime = 1_12_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(beforeSchedule)
            personalSchedule.fixedSchedules.add(schedule)
            personalSchedule.fixedSchedules.add(afterSchedule)

            //when && then
            StepVerifier.create(personalSchedule.deleteFixedScheduleById(schedule.id))
                .expectNextCount(1)
                .verifyComplete()

            Assertions.assertEquals(2, personalSchedule.fixedSchedules.size)
        }

        @Test
        @DisplayName("여러개의 요소가 있는 스케줄에서 없는 고정 스케줄을 삭제")
        fun deleteNotExistFixedScheduleFromManyElementScheduleReturnSuccess(){
            //given
            val beforeSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_10_00L,
                endTime = 1_10_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val schedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_11_00L,
                endTime = 1_11_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val afterSchedule = Schedule(
                id = ObjectId.get(),
                startTime = 1_12_00L,
                endTime = 1_12_59L,
                title = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(beforeSchedule)
            personalSchedule.fixedSchedules.add(schedule)
            personalSchedule.fixedSchedules.add(afterSchedule)

            //when && then
            StepVerifier.create(personalSchedule.deleteFixedScheduleById(ObjectId.get()))
                .expectErrorMatches {
                    it is ScheduleNotFoundException
                }
                .verify()

            Assertions.assertEquals(3, personalSchedule.fixedSchedules.size)
        }
    }
}
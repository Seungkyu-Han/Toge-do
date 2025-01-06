package vp.togedo.model.documents.personalSchedule

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.personalSchedule.NotFoundPersonaScheduleException
import java.util.*

class PersonalScheduleTest{

    private val userId = ObjectId.get()
    private lateinit var personalSchedule: PersonalSchedule

    private val flexiblePersonalSchedule = PersonalScheduleElement(
        startTime = "1001010000",
        endTime = "1001010001",
        name = UUID.randomUUID().toString(),
        color = UUID.randomUUID().toString()
    )

    private val fixedPersonalSchedule = PersonalScheduleElement(
        startTime = "10000",
        endTime = "10001",
        name = UUID.randomUUID().toString(),
        color = UUID.randomUUID().toString()
    )

    @Nested
    inner class FindFixedPersonalScheduleIndexById{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("하나의 요소가 있는 리스트에서 고정 스케줄의 인덱스를 탐색")
        fun findFixedIndexFromOneElementListReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(fixedPersonalSchedule)

            //when
            val index = personalSchedule.findFixedPersonalScheduleIndexById(fixedPersonalSchedule.id)

            //then
            Assertions.assertEquals(0, index)
        }

        @Test
        @DisplayName("10개의 요소가 있는 리스트에서 고정 스케줄의 인덱스를 탐색")
        fun findFixedIndexFromTenElementListReturnSuccess(){
            //given
            for(i in 0..8)
                personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                    startTime = "10${i}00",
                    endTime = "10${i}01",
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            personalSchedule.fixedSchedules.add(fixedPersonalSchedule)

            //when
            val index = personalSchedule.findFixedPersonalScheduleIndexById(fixedPersonalSchedule.id)

            //then
            Assertions.assertEquals(9, index)
        }

        @Test
        @DisplayName("빈 리스트에서 고정 스케줄의 인덱스를 탐색")
        fun findFixedIndexFromEmptyListReturnException(){
            //given

            //when && then
            Assertions.assertThrows(NotFoundPersonaScheduleException::class.java){
                personalSchedule.findFixedPersonalScheduleIndexById(fixedPersonalSchedule.id)
            }
        }

        @Test
        @DisplayName("10개의 요소가 있는 리스트에서 없는 고정 스케줄의 인덱스를 탐색")
        fun findFlexibleIndexFromNotExistTenElementListReturnException(){
            //given
            for(i in 0..9)
                personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                    startTime = "10${i}00",
                    endTime = "10${i}01",
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))

            //when && then
            Assertions.assertThrows(NotFoundPersonaScheduleException::class.java){
                personalSchedule.findFixedPersonalScheduleIndexById(fixedPersonalSchedule.id)
            }
        }
    }

    @Nested
    inner class FindFlexiblePersonalScheduleIndexById{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("하나의 요소가 있는 리스트에서 유동 스케줄의 인덱스를 탐색")
        fun findFlexibleIndexFromOneElementListReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(flexiblePersonalSchedule)

            //when
            val index = personalSchedule.findFlexiblePersonalScheduleIndexById(flexiblePersonalSchedule.id)

            //then
            Assertions.assertEquals(0, index)
        }

        @Test
        @DisplayName("10개의 요소가 있는 리스트에서 유동 스케줄의 인덱스를 탐색")
        fun findFlexibleIndexFromTenElementListReturnSuccess(){
            //given
            for(i in 1..9)
                personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                    startTime = "10010${i}0000",
                    endTime = "10010${i}0001",
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            personalSchedule.flexibleSchedules.add(flexiblePersonalSchedule)

            //when
            val index = personalSchedule.findFlexiblePersonalScheduleIndexById(flexiblePersonalSchedule.id)

            //then
            Assertions.assertEquals(9, index)
        }

        @Test
        @DisplayName("빈 리스트에서 유동 스케줄의 인덱스를 탐색")
        fun findFlexibleIndexFromEmptyListReturnException(){
            //given

            //when && then
            Assertions.assertThrows(NotFoundPersonaScheduleException::class.java){
                personalSchedule.findFlexiblePersonalScheduleIndexById(flexiblePersonalSchedule.id)
            }
        }

        @Test
        @DisplayName("10개의 요소가 있는 리스트에서 없는 요동 스케줄의 인덱스를 탐색")
        fun findFlexibleIndexFromNotExistTenElementListReturnException(){
            //given
            for(i in 1..10)
                personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                    startTime = "10010${i}0000",
                    endTime = "10010${i}0001",
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))

            //when && then
            Assertions.assertThrows(NotFoundPersonaScheduleException::class.java){
                personalSchedule.findFlexiblePersonalScheduleIndexById(flexiblePersonalSchedule.id)
            }
        }
    }
}
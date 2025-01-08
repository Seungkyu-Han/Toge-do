package vp.togedo.model.documents.personalSchedule

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.personalSchedule.ConflictPersonalScheduleException
import vp.togedo.model.exception.personalSchedule.NotFoundPersonaScheduleException
import java.util.*

class PersonalScheduleTest{

    private val userId = ObjectId.get()
    private lateinit var personalSchedule: PersonalSchedule

    private val flexiblePersonalSchedule = PersonalScheduleElement(
        startTime = "2501081000",
        endTime = "2501081059",
        name = UUID.randomUUID().toString(),
        color = UUID.randomUUID().toString()
    )

    private val fixedPersonalSchedule = PersonalScheduleElement(
        startTime = "20000",
        endTime = "20001",
        name = UUID.randomUUID().toString(),
        color = UUID.randomUUID().toString()
    )

    @Nested
    inner class ModifyFlexiblePersonalScheduleElement{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("존재하는 유동 스케줄을 변경")
        fun modifyFlexibleScheduleExistedReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(flexiblePersonalSchedule)
            val newFlexiblePersonalSchedule = PersonalScheduleElement(
                id = flexiblePersonalSchedule.id,
                startTime = "2601081000",
                endTime = "2601081000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            personalSchedule.modifyFlexiblePersonalScheduleElement(newFlexiblePersonalSchedule)

            //then
            Assertions.assertEquals(
                newFlexiblePersonalSchedule,
                personalSchedule.flexibleSchedules.find { it.id == flexiblePersonalSchedule.id }
            )
        }

        @Test
        @DisplayName("존재하지 않는 유동 스케줄을 변경")
        fun modifyFlexibleScheduleNotExistReturnSuccess(){
            //given
            val newFlexiblePersonalSchedule = PersonalScheduleElement(
                id = flexiblePersonalSchedule.id,
                startTime = "2601081000",
                endTime = "2601081000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            Assertions.assertThrows(NotFoundPersonaScheduleException::class.java) {
                personalSchedule.modifyFlexiblePersonalScheduleElement(newFlexiblePersonalSchedule)
            }

            Assertions.assertTrue(personalSchedule.flexibleSchedules.isEmpty())
        }

        @Test
        @DisplayName("스케줄을 변경하는 도중에 에러가 발생하고 롤백")
        fun modifyFlexibleScheduleThrowExceptionAndRollbackReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(
                PersonalScheduleElement(
                    startTime = "2601081000",
                    endTime = "2601081000",
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )
            personalSchedule.flexibleSchedules.add(flexiblePersonalSchedule)
            val newFlexiblePersonalSchedule = PersonalScheduleElement(
                id = flexiblePersonalSchedule.id,
                startTime = "2601081000",
                endTime = "2601081000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java) {
                personalSchedule.modifyFlexiblePersonalScheduleElement(newFlexiblePersonalSchedule)
            }

            Assertions.assertEquals(2, personalSchedule.flexibleSchedules.size)
            Assertions.assertEquals(flexiblePersonalSchedule, personalSchedule.flexibleSchedules.find { it.id == flexiblePersonalSchedule.id })
        }
    }

    @Nested
    inner class DeleteFixedPersonalScheduleElementById{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("하나만 존재하는 고정 일정 리스트에서 요소를 삭제")
        fun deleteFixedScheduleFromOneElementListReturnSuccess(){
            //given
            personalSchedule.fixedSchedules.add(fixedPersonalSchedule)

            //when
            personalSchedule.deleteFixedPersonalScheduleElementById(fixedPersonalSchedule.id)

            //then
            Assertions.assertFalse(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
            Assertions.assertEquals(0, personalSchedule.fixedSchedules.size)
        }

        @Test
        @DisplayName("빈 리스트에서 고정 일정을 삭제 시도")
        fun deleteFixedScheduleFromEmptyListReturnException(){
            //given

            //when && then
            Assertions.assertThrows(NotFoundPersonaScheduleException::class.java) {
                personalSchedule.deleteFixedPersonalScheduleElementById(fixedPersonalSchedule.id) }
        }

        @Test
        @DisplayName("10개가 존재하는 고정 일정 리스트에서 요소를 삭제")
        fun deleteFixedScheduleFrom10ElementListReturnSuccess(){
            //given
            for (i in 1..9){
                personalSchedule.fixedSchedules.add(
                    PersonalScheduleElement(
                        startTime = "1000${i}",
                        endTime = "1000${i}",
                        name = UUID.randomUUID().toString(),
                        color = UUID.randomUUID().toString()
                    )
                )
            }
            personalSchedule.fixedSchedules.add(fixedPersonalSchedule)

            //when
            personalSchedule.deleteFixedPersonalScheduleElementById(fixedPersonalSchedule.id)

            //then
            Assertions.assertEquals(9, personalSchedule.fixedSchedules.size)
            Assertions.assertFalse(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }
    }

    @Nested
    inner class DeleteFlexiblePersonalScheduleElementById{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("하나만 존재하는 유동 일정 리스트에서 요소를 삭제")
        fun deleteFlexibleScheduleFromOneElementListReturnSuccess(){
            //given
            personalSchedule.flexibleSchedules.add(flexiblePersonalSchedule)

            //when
            personalSchedule.deleteFlexiblePersonalScheduleElementById(flexiblePersonalSchedule.id)

            //then
            Assertions.assertFalse(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
            Assertions.assertEquals(0, personalSchedule.flexibleSchedules.size)
        }

        @Test
        @DisplayName("빈 리스트에서 유동 일정을 삭제 시도")
        fun deleteFlexibleScheduleFromEmptyListReturnException(){
            //given

            //when && then
            Assertions.assertThrows(NotFoundPersonaScheduleException::class.java) {
                personalSchedule.deleteFlexiblePersonalScheduleElementById(flexiblePersonalSchedule.id) }
        }

        @Test
        @DisplayName("10개가 존재하는 유동 일정 리스트에서 요소를 삭제")
        fun deleteFlexibleScheduleFrom10ElementListReturnSuccess(){
            //given
            for (i in 1..9){
                personalSchedule.flexibleSchedules.add(
                    PersonalScheduleElement(
                        startTime = "24010${i}1000",
                        endTime = "24010${i}1059",
                        name = UUID.randomUUID().toString(),
                        color = UUID.randomUUID().toString()
                    )
                )
            }
            personalSchedule.flexibleSchedules.add(flexiblePersonalSchedule)

            //when
            personalSchedule.deleteFlexiblePersonalScheduleElementById(flexiblePersonalSchedule.id)

            //then
            Assertions.assertEquals(9, personalSchedule.flexibleSchedules.size)
            Assertions.assertFalse(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }
    }

    @Nested
    inner class AddFixedPersonalScheduleElement{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("빈 리스트에 고정 스케줄을 삽입")
        fun addFixedScheduleInEmptyListReturnSuccess(){
            //given

            //when
            personalSchedule.addFixedPersonalScheduleElement(fixedPersonalSchedule)

            //then
            Assertions.assertEquals(1, personalSchedule.fixedSchedules.size)
            Assertions.assertTrue(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }

        @Test
        @DisplayName("앞에 하나의 고정 스케줄이 있는 리스트에 고정 스케줄을 삽입")
        fun addFixedPersonalScheduleInOneElementAtFrontReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(beforePersonalScheduleElement)

            //when
            personalSchedule.addFixedPersonalScheduleElement(fixedPersonalSchedule)

            //then
            Assertions.assertEquals(2, personalSchedule.fixedSchedules.size)
            Assertions.assertTrue(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }

        @Test
        @DisplayName("뒤에 하나의 고정 스케줄이 있는 리스트에 고정 스케줄을 삽입")
        fun addFixedPersonalScheduleInOneElementAtBehindReturnSuccess(){
            //given
            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "30000",
                endTime = "30001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(afterPersonalScheduleElement)

            //when
            personalSchedule.addFixedPersonalScheduleElement(fixedPersonalSchedule)

            //then
            Assertions.assertEquals(2, personalSchedule.fixedSchedules.size)
            Assertions.assertTrue(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }


        @Test
        @DisplayName("앞뒤에 하나씩 고정 스케줄이 있는 리스트에 고정 스케줄을 삽입")
        fun addFixedPersonalScheduleAtFrontAndBehindReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "30000",
                endTime = "30001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(beforePersonalScheduleElement)
            personalSchedule.fixedSchedules.add(afterPersonalScheduleElement)

            //when
            personalSchedule.addFixedPersonalScheduleElement(fixedPersonalSchedule)

            //then
            Assertions.assertEquals(3, personalSchedule.fixedSchedules.size)
            Assertions.assertTrue(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }

        @Test
        @DisplayName("앞뒤에 40개씩 고정 스케줄이 있는 리스트에 고정 스케줄을 삽입")
        fun addFixedPersonalScheduleAtFront40AndBehind40ReturnSuccess(){
            //given
            for(i in 1..40){
                personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                    startTime = (10000 + i).toString(),
                    endTime = (10100 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }

            for(i in 1..40){
                personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                    startTime = (30000 + i).toString(),
                    endTime = (30100 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }

            //when
            personalSchedule.addFixedPersonalScheduleElement(fixedPersonalSchedule)

            //then
            Assertions.assertEquals(81, personalSchedule.fixedSchedules.size)
            Assertions.assertTrue(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }


        @Test
        @DisplayName("시작 시간이 겹치는 리스트에 고정 스케줄을 삽입")
        fun addFixedScheduleAtConflictStartTimeReturnException(){
            //given
            personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                startTime = "20000",
                endTime = "20059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            //when
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.addFixedPersonalScheduleElement(
                    personalScheduleElement = fixedPersonalSchedule)
            }

            //then
            Assertions.assertFalse(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }


        @Test
        @DisplayName("전 스케줄의 종료시간과 충돌하는 리스트에 고정 스케줄을 삽입")
        fun addFixedScheduleAtConflictBeforeScheduleReturnException(){
            //given
            personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                startTime = "20000",
                endTime = "20100",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            val fixedPersonalSchedule = PersonalScheduleElement(
                startTime = "20100",
                endTime = "20159",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.addFixedPersonalScheduleElement(
                    personalScheduleElement = fixedPersonalSchedule)
            }

            //then
            Assertions.assertFalse(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }

        @Test
        @DisplayName("뒤 스케줄의 시작시간과 충돌하는 리스트에 유동 스케줄을 삽입")
        fun addFixedScheduleAtConflictAfterScheduleReturnException(){
            //given
            personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                startTime = "20100",
                endTime = "20159",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            val fixedPersonalSchedule = PersonalScheduleElement(
                startTime = "20000",
                endTime = "20100",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.addFixedPersonalScheduleElement(
                    personalScheduleElement = fixedPersonalSchedule)
            }

            //then
            Assertions.assertFalse(personalSchedule.fixedSchedules.contains(fixedPersonalSchedule))
        }

    }

    @Nested
    inner class AddFlexiblePersonalScheduleElement{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("빈 리스트에 유동 스케줄을 삽입")
        fun addFlexibleScheduleInEmptyListReturnSuccess(){
            //given

            //when
            personalSchedule.addFlexiblePersonalScheduleElement(flexiblePersonalSchedule)

            //then
            Assertions.assertEquals(1, personalSchedule.flexibleSchedules.size)
            Assertions.assertTrue(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }

        @Test
        @DisplayName("앞에 하나의 유동 스케줄이 있는 리스트에 유동 스케줄을 삽입")
        fun addFlexiblePersonalScheduleInOneElementAtFrontReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501071000",
                endTime = "2501071059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.flexibleSchedules.add(beforePersonalScheduleElement)

            //when
            personalSchedule.addFlexiblePersonalScheduleElement(flexiblePersonalSchedule)

            //then
            Assertions.assertEquals(2, personalSchedule.flexibleSchedules.size)
            Assertions.assertTrue(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }

        @Test
        @DisplayName("뒤에 하나의 유동 스케줄이 있는 리스트에 유동 스케줄을 삽입")
        fun addFlexiblePersonalScheduleInOneElementAtBehindReturnSuccess(){
            //given
            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501091000",
                endTime = "2501091059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.flexibleSchedules.add(afterPersonalScheduleElement)

            //when
            personalSchedule.addFlexiblePersonalScheduleElement(flexiblePersonalSchedule)

            //then
            Assertions.assertEquals(2, personalSchedule.flexibleSchedules.size)
            Assertions.assertTrue(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }


        @Test
        @DisplayName("앞뒤에 하나씩 유동 스케줄이 있는 리스트에 유동 스케줄을 삽입")
        fun addFlexiblePersonalScheduleAtFrontAndBehindReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501071000",
                endTime = "2501071059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501091000",
                endTime = "2501091059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.flexibleSchedules.add(beforePersonalScheduleElement)
            personalSchedule.flexibleSchedules.add(afterPersonalScheduleElement)

            //when
            personalSchedule.addFlexiblePersonalScheduleElement(flexiblePersonalSchedule)

            //then
            Assertions.assertEquals(3, personalSchedule.flexibleSchedules.size)
            Assertions.assertTrue(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }

        @Test
        @DisplayName("앞뒤에 40개씩 유동 스케줄이 있는 리스트에 유동 스케줄을 삽입")
        fun addFlexiblePersonalScheduleAtFront40AndBehind40ReturnSuccess(){
            //given
            for(i in 1..40){
                personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                    startTime = (2501071000 + i).toString(),
                    endTime = (2501071000 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }

            for(i in 1..40){
                personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                    startTime = (2501091000 + i).toString(),
                    endTime = (2501091000 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }

            //when
            personalSchedule.addFlexiblePersonalScheduleElement(flexiblePersonalSchedule)

            //then
            Assertions.assertEquals(81, personalSchedule.flexibleSchedules.size)
            Assertions.assertTrue(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }


        @Test
        @DisplayName("시작 시간이 겹치는 리스트에 유동 스케줄을 삽입")
        fun addFlexibleScheduleAtConflictStartTimeReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                startTime = "2501081000",
                endTime = "2501081100",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            //when
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.addFlexiblePersonalScheduleElement(
                    personalScheduleElement = flexiblePersonalSchedule)
            }

            //then
            Assertions.assertFalse(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }


        @Test
        @DisplayName("전 스케줄의 종료시간과 충돌하는 리스트에 유동 스케줄을 삽입")
        fun addFixedScheduleAtConflictBeforeScheduleReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                startTime = "2501080900",
                endTime = "2501081000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            //when
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.addFlexiblePersonalScheduleElement(
                    personalScheduleElement = flexiblePersonalSchedule)
            }

            //then
            Assertions.assertFalse(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }

        @Test
        @DisplayName("뒤 스케줄의 시작시간과 충돌하는 리스트에 유동 스케줄을 삽입")
        fun addFixedScheduleAtConflictAfterScheduleReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                startTime = "2501080900",
                endTime = "2501081000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            val flexiblePersonalSchedule = PersonalScheduleElement(
                startTime = "2501081000",
                endTime = "2501081059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            //when
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.addFlexiblePersonalScheduleElement(
                    personalScheduleElement = flexiblePersonalSchedule)
            }

            //then
            Assertions.assertFalse(personalSchedule.flexibleSchedules.contains(flexiblePersonalSchedule))
        }
    }

    @Nested
    inner class GetSortedIndex{
        @BeforeEach
        fun setUp(){
            personalSchedule = PersonalSchedule(id = userId)
        }

        @Test
        @DisplayName("빈 리스트에 고정 스케줄 인덱스를 탐색")
        fun getSortedIndexFixedScheduleFromEmptyListReturnSuccess(){
            //given
            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = fixedPersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("앞에 하나의 고정 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFixedScheduleFromOneElementAtFrontReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(beforePersonalScheduleElement)
            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = fixedPersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("뒤에 하나의 고정 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFixedScheduleFromOneElementAtBehindReturnSuccess(){
            //given
            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "30000",
                endTime = "30001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(afterPersonalScheduleElement)
            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = fixedPersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("앞뒤에 하나씩 고정 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFixedScheduleFromOneElementAtFrontAndBehindReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "10000",
                endTime = "10001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "30000",
                endTime = "30001",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.fixedSchedules.add(beforePersonalScheduleElement)
            personalSchedule.fixedSchedules.add(afterPersonalScheduleElement)
            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = fixedPersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(1, result)
        }

        @Test
        @DisplayName("앞뒤에 40개씩 고정 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFixedScheduleFromOneElementAtFront40AndBehind40ReturnSuccess(){
            //given
            for(i in 1..40){
                personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                    startTime = (10000 + i).toString(),
                    endTime = (10100 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }

            for(i in 1..40){
                personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                    startTime = (30000 + i).toString(),
                    endTime = (30100 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }
            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = fixedPersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(40, result)
        }

        @Test
        @DisplayName("시작 시간이 겹치는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFixedScheduleFromConflictStartTimeReturnException(){
            //given
            personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                startTime = "20000",
                endTime = "20059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))
            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when && then
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.getSortedIndex(
                    personalScheduleElement = fixedPersonalSchedule,
                    personalScheduleEnum = personalScheduleEnum)
            }
        }

        @Test
        @DisplayName("전 스케줄의 종료시간과 탐색하는 스케줄의 시작시간이 충돌하는 경우")
        fun getSortedIndexFixedScheduleFromConflictBeforeScheduleReturnException(){
            //given
            personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                startTime = "20000",
                endTime = "20100",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            val fixedPersonalSchedule = PersonalScheduleElement(
                startTime = "20100",
                endTime = "20159",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when && then
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.getSortedIndex(
                    personalScheduleElement = fixedPersonalSchedule,
                    personalScheduleEnum = personalScheduleEnum)
            }
        }

        @Test
        @DisplayName("뒤 스케줄의 시작시간과 탐색하는 스케줄의 종료시간이 충돌하는 경우")
        fun getSortedIndexFixedScheduleFromConflictAfterScheduleReturnException(){
            //given
            personalSchedule.fixedSchedules.add(PersonalScheduleElement(
                startTime = "20100",
                endTime = "20159",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            val fixedPersonalSchedule = PersonalScheduleElement(
                startTime = "20000",
                endTime = "20100",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val personalScheduleEnum = PersonalScheduleEnum.FIXED_PERSONAL_SCHEDULE

            //when && then
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.getSortedIndex(
                    personalScheduleElement = fixedPersonalSchedule,
                    personalScheduleEnum = personalScheduleEnum)
            }
        }

        @Test
        @DisplayName("빈 리스트에 유동 스케줄 인덱스를 탐색")
        fun getSortedIndexFlexibleScheduleFromEmptyListReturnSuccess(){
            //given
            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = flexiblePersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(0, result)
        }

        @Test
        @DisplayName("앞에 하나의 고정 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFlexibleScheduleFromOneElementAtFrontReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501071000",
                endTime = "2501071059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.flexibleSchedules.add(beforePersonalScheduleElement)
            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = flexiblePersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(1, result)
        }


        @Test
        @DisplayName("뒤에 하나의 유동 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFlexibleScheduleFromOneElementAtBehindReturnSuccess(){
            //given
            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501091000",
                endTime = "2501091059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.flexibleSchedules.add(afterPersonalScheduleElement)
            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = flexiblePersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(0, result)
        }


        @Test
        @DisplayName("앞뒤에 하나씩 유동 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFlexibleScheduleFromOneElementAtFrontAndBehindReturnSuccess(){
            //given
            val beforePersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501071000",
                endTime = "2501071059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val afterPersonalScheduleElement = PersonalScheduleElement(
                startTime = "2501091000",
                endTime = "2501091059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )
            personalSchedule.flexibleSchedules.add(beforePersonalScheduleElement)
            personalSchedule.flexibleSchedules.add(afterPersonalScheduleElement)
            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = flexiblePersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(1, result)
        }


        @Test
        @DisplayName("앞뒤에 40개씩 유동 스케줄이 있는 리스트에서 인덱스를 탐색")
        fun getSortedIndexFlexibleScheduleFromOneElementAtFront40AndBehind40ReturnSuccess(){
            //given
            for(i in 1..40){
                personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                    startTime = (2501071000 + i).toString(),
                    endTime = (2501071000 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }

            for(i in 1..40){
                personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                    startTime = (2501091000 + i).toString(),
                    endTime = (2501091000 + i).toString(),
                    name = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ))
            }
            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when
            val result = personalSchedule.getSortedIndex(
                personalScheduleElement = flexiblePersonalSchedule,
                personalScheduleEnum = personalScheduleEnum)

            //then
            Assertions.assertEquals(40, result)
        }

        @Test
        @DisplayName("시작 시간이 겹치는 유동 리스트에서 인덱스를 탐색")
        fun getSortedIndexFlexibleScheduleFromConflictStartTimeReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                startTime = "2501081000",
                endTime = "2501081100",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))
            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when && then
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.getSortedIndex(
                    personalScheduleElement = flexiblePersonalSchedule,
                    personalScheduleEnum = personalScheduleEnum)
            }
        }

        @Test
        @DisplayName("전 스케줄의 종료시간과 탐색하는 스케줄의 시작시간이 충돌하는 경우")
        fun getSortedIndexFlexibleScheduleFromConflictBeforeScheduleReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                startTime = "2501080900",
                endTime = "2501081000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))
            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when && then
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.getSortedIndex(
                    personalScheduleElement = flexiblePersonalSchedule,
                    personalScheduleEnum = personalScheduleEnum)
            }
        }

        @Test
        @DisplayName("뒤 스케줄의 시작시간과 탐색하는 스케줄의 종료시간이 충돌하는 경우")
        fun getSortedIndexFlexibleScheduleFromConflictAfterScheduleReturnException(){
            //given
            personalSchedule.flexibleSchedules.add(PersonalScheduleElement(
                startTime = "2501080900",
                endTime = "2501081000",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            ))

            val flexiblePersonalSchedule = PersonalScheduleElement(
                startTime = "2501081000",
                endTime = "2501081059",
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString()
            )

            val personalScheduleEnum = PersonalScheduleEnum.FLEXIBLE_PERSONAL_SCHEDULE

            //when && then
            Assertions.assertThrows(ConflictPersonalScheduleException::class.java){
                personalSchedule.getSortedIndex(
                    personalScheduleElement = flexiblePersonalSchedule,
                    personalScheduleEnum = personalScheduleEnum)
            }
        }
    }

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
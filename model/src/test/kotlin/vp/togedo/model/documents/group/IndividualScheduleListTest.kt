package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.group.NotFoundIndividualScheduleException
import java.util.UUID

class IndividualScheduleListTest{

    private lateinit var individualScheduleList: IndividualScheduleList

    @Nested
    inner class AddIndividualSchedule{
        @BeforeEach
        fun setUp() {
            individualScheduleList = IndividualScheduleList()
        }

        @Test
        @DisplayName("빈 리스트에 새로운 일정을 생성")
        fun addIndividualScheduleInEmptyListReturnSuccess(){
            //given
            val startTime = UUID.randomUUID().toString()
            val endTime = UUID.randomUUID().toString()

            //when
            individualScheduleList.addIndividualSchedule(
                startTime = startTime,
                endTime = endTime,
            )

            //then
            Assertions.assertEquals(1, individualScheduleList.individualSchedules.size)
            Assertions.assertEquals(startTime, individualScheduleList.individualSchedules.first().startTime)
            Assertions.assertEquals(endTime, individualScheduleList.individualSchedules.first().endTime)
        }
    }

    @Nested
    inner class RemoveIndividualSchedule{
        @BeforeEach
        fun setUp() {
            individualScheduleList = IndividualScheduleList()
        }

        @Test
        @DisplayName("하나 존재하는 일정을 삭제")
        fun removeIndividualScheduleFromOneElementListReturnSuccess(){
            //given
            val individualScheduleElement = IndividualScheduleElement(
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
            )
            individualScheduleList.individualSchedules.add(individualScheduleElement)

            //when
            individualScheduleList.removeIndividualScheduleById(individualScheduleElement.id)

            //then
            Assertions.assertEquals(0, individualScheduleList.individualSchedules.size)
        }

        @Test
        @DisplayName("빈 리스트에서 일정을 삭제")
        fun removeIndividualScheduleFromEmptyListReturnException(){
            //when && then
            Assertions.assertThrows(NotFoundIndividualScheduleException::class.java) {
                individualScheduleList.removeIndividualScheduleById(ObjectId.get())
            }
        }
    }
}
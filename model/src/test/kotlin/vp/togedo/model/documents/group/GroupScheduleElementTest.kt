package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import java.util.*

class GroupScheduleElementTest{

    private lateinit var groupScheduleElement: GroupScheduleElement

    private val userId = ObjectId.get()

    @Nested
    inner class UpdateGroupScheduleElement{

        @BeforeEach
        fun setUp() {
            groupScheduleElement = GroupScheduleElement(
                name = UUID.randomUUID().toString(),
                startDate = UUID.randomUUID().toString(),
                endDate = UUID.randomUUID().toString(),
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                scheduleMember = mutableListOf(userId)
            )
        }

        @Test
        @DisplayName("그룹 요소의 정보를 수정")
        fun updateGroupScheduleElementReturnSuccess(){
            //given
            val newName = UUID.randomUUID().toString()
            val newStartDate = UUID.randomUUID().toString()
            val newEndDate = UUID.randomUUID().toString()
            val newStartTime = UUID.randomUUID().toString()
            val newEndTime = UUID.randomUUID().toString()

            //when
            groupScheduleElement.updateGroupScheduleElement(
                name = newName,
                startDate = newStartDate,
                endDate = newEndDate,
                startTime = newStartTime,
                endTime = newEndTime,
            )

            //then
            Assertions.assertEquals(newName, groupScheduleElement.name)
            Assertions.assertEquals(newStartDate, groupScheduleElement.startDate)
            Assertions.assertEquals(newEndDate, groupScheduleElement.endDate)
            Assertions.assertEquals(newStartTime, groupScheduleElement.startTime)
            Assertions.assertEquals(newEndTime, groupScheduleElement.endTime)
        }
    }
}
package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.group.NotFoundMemberException
import vp.togedo.model.exception.group.ScheduleNotRequestedException
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
                scheduleMember = mutableSetOf(userId)
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

    @Nested
    inner class RequestConfirmSchedule{
        @BeforeEach
        fun setUp() {
            groupScheduleElement = GroupScheduleElement(
                name = UUID.randomUUID().toString(),
                startDate = UUID.randomUUID().toString(),
                endDate = UUID.randomUUID().toString(),
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                scheduleMember = mutableSetOf(userId)
            )
        }

        @Test
        @DisplayName("일정 확인을 요청")
        fun requestConfirmScheduleReturnSuccess(){
            //given
            val confirmedStartDate = UUID.randomUUID().toString()
            val confirmedEndDate = UUID.randomUUID().toString()

            //when
            groupScheduleElement.requestConfirmSchedule(
                confirmedStartDate = confirmedStartDate,
                confirmedEndDate = confirmedEndDate,
                userId = userId
            )

            //then
            Assertions.assertEquals(confirmedStartDate, groupScheduleElement.confirmedStartDate)
            Assertions.assertEquals(confirmedEndDate, groupScheduleElement.confirmedEndDate)
            Assertions.assertTrue(groupScheduleElement.confirmedUser.contains(userId))
            Assertions.assertEquals(GroupScheduleStateEnum.REQUESTED, groupScheduleElement.state)
        }

        @Test
        @DisplayName("CONFIRMED 상태에서 일정 확인을 요청")
        fun requestConfirmScheduleFromConfirmedStateReturnSuccess(){
            //given
            groupScheduleElement.state = GroupScheduleStateEnum.CONFIRMED
            val confirmedStartDate = UUID.randomUUID().toString()
            val confirmedEndDate = UUID.randomUUID().toString()

            //when
            groupScheduleElement.requestConfirmSchedule(
                confirmedStartDate = confirmedStartDate,
                confirmedEndDate = confirmedEndDate,
                userId = userId
            )

            //then
            Assertions.assertEquals(confirmedStartDate, groupScheduleElement.confirmedStartDate)
            Assertions.assertEquals(confirmedEndDate, groupScheduleElement.confirmedEndDate)
            Assertions.assertTrue(groupScheduleElement.confirmedUser.contains(userId))
            Assertions.assertEquals(GroupScheduleStateEnum.REQUESTED, groupScheduleElement.state)
        }
    }

    @Nested
    inner class CheckRequestedSchedule{

        private val user1 = ObjectId.get()

        @BeforeEach
        fun setUp() {
            groupScheduleElement = GroupScheduleElement(
                name = UUID.randomUUID().toString(),
                startDate = UUID.randomUUID().toString(),
                endDate = UUID.randomUUID().toString(),
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                scheduleMember = mutableSetOf(userId, user1)
            )
        }

        @Test
        @DisplayName("해당 유저가 스케줄을 확인")
        fun checkRequestedScheduleToApproveReturnSuccess(){
            //given
            groupScheduleElement.state = GroupScheduleStateEnum.REQUESTED
            groupScheduleElement.confirmedStartDate = UUID.randomUUID().toString()
            groupScheduleElement.confirmedEndDate = UUID.randomUUID().toString()

            //when
            groupScheduleElement.checkRequestedSchedule(
                userId = userId,
                isApprove = true
            )

            //then
            Assertions.assertTrue(groupScheduleElement.confirmedUser.contains(userId))
            Assertions.assertEquals(GroupScheduleStateEnum.REQUESTED, groupScheduleElement.state)
        }

        @Test
        @DisplayName("마지막 유저가 승인하여 해당 일정이 CONFIRMED 상태로 변경")
        fun checkRequestedScheduleToApproveAndChangeStateToConfirmedReturnSuccess(){
            //given
            groupScheduleElement.state = GroupScheduleStateEnum.REQUESTED
            groupScheduleElement.confirmedUser.add(user1)
            groupScheduleElement.confirmedStartDate = UUID.randomUUID().toString()
            groupScheduleElement.confirmedEndDate = UUID.randomUUID().toString()

            //when
            groupScheduleElement.checkRequestedSchedule(
                userId = userId,
                isApprove = true
            )

            //then
            Assertions.assertTrue(groupScheduleElement.confirmedUser.contains(userId))
            Assertions.assertEquals(GroupScheduleStateEnum.CONFIRMED, groupScheduleElement.state)
        }

        @Test
        @DisplayName("해당 유저가 거절하여 일정이 REJECTED 상태로 변경")
        fun checkRequestedScheduleToNotApproveAndChangeStateToRejectedReturnSuccess(){
            //given
            groupScheduleElement.state = GroupScheduleStateEnum.REQUESTED
            groupScheduleElement.confirmedUser.add(user1)
            groupScheduleElement.confirmedStartDate = UUID.randomUUID().toString()
            groupScheduleElement.confirmedEndDate = UUID.randomUUID().toString()

            //when
            groupScheduleElement.checkRequestedSchedule(
                userId = userId,
                isApprove = false
            )

            //then
            Assertions.assertFalse(groupScheduleElement.confirmedUser.contains(userId))
            Assertions.assertEquals(GroupScheduleStateEnum.REJECTED, groupScheduleElement.state)
        }

        @Test
        @DisplayName("승인된 스케줄을 거절하여 REJECTED 상태로 변경")
        fun checkRequestedScheduleToNotApproveFromConfirmedReturnSuccess(){
            groupScheduleElement.state = GroupScheduleStateEnum.CONFIRMED
            groupScheduleElement.confirmedUser.addAll(listOf(user1, userId))
            groupScheduleElement.confirmedStartDate = UUID.randomUUID().toString()
            groupScheduleElement.confirmedEndDate = UUID.randomUUID().toString()

            //when
            groupScheduleElement.checkRequestedSchedule(
                userId = userId,
                isApprove = false
            )

            //then
            Assertions.assertFalse(groupScheduleElement.confirmedUser.contains(userId))
            Assertions.assertEquals(GroupScheduleStateEnum.REJECTED, groupScheduleElement.state)
        }

        @Test
        @DisplayName("일정에 속하지 않는 유저가 시도")
        fun checkRequestedScheduleToNotMemberReturnException(){
            //given
            groupScheduleElement.state = GroupScheduleStateEnum.REQUESTED
            groupScheduleElement.confirmedStartDate = UUID.randomUUID().toString()
            groupScheduleElement.confirmedEndDate = UUID.randomUUID().toString()
            val newUser = ObjectId.get()

            //when
            Assertions.assertThrows(NotFoundMemberException::class.java) {
                groupScheduleElement.checkRequestedSchedule(
                    userId = newUser,
                    isApprove = true
                )
            }
        }

        @Test
        @DisplayName("요청되지 않은 스케줄을 확인 시도")
        fun checkRequestedScheduleToNotRequestedReturnException(){
            //given

            //when
            Assertions.assertThrows(ScheduleNotRequestedException::class.java) {
                groupScheduleElement.checkRequestedSchedule(
                    userId = userId,
                    isApprove = true
                )
            }
        }
    }
}
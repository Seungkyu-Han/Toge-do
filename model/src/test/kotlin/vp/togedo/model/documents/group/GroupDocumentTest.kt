package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.group.AlreadyMemberException
import vp.togedo.model.exception.group.NotFoundGroupScheduleException
import vp.togedo.model.exception.group.NotFoundMemberException
import java.util.UUID

class GroupDocumentTest{

    private lateinit var groupDocument: GroupDocument

    private val userId = ObjectId.get()

    @Nested
    inner class UpdateGroup{

        @BeforeEach
        fun setUp() {
            groupDocument = GroupDocument(
                name = UUID.randomUUID().toString(),
                members = mutableSetOf(userId)
            )
        }

        @Test
        @DisplayName("해당 그룹의 이름을 성공적으로 변경")
        fun updateGroupNameReturnSuccess(){
            //given
            val newName = UUID.randomUUID().toString()

            //when
            groupDocument.updateGroup(
                name = newName
            )

            //then
            Assertions.assertEquals(newName, groupDocument.name)
        }
    }

    @Nested
    inner class AddMember{
        @BeforeEach
        fun setUp() {
            groupDocument = GroupDocument(
                name = UUID.randomUUID().toString(),
                members = mutableSetOf(userId)
            )
        }

        @Test
        @DisplayName("해당 그룹에 없는 멤버를 추가")
        fun addMemberToNotExistMemberReturnsSuccess(){
            //given
            val newMember = ObjectId.get()

            //when
            groupDocument.addMember(newMember)

            //then
            Assertions.assertTrue(groupDocument.members.contains(newMember))
        }

        @Test
        @DisplayName("해당 그룹에 존재하는 멤버를 추가")
        fun addMemberToExistMemberReturnException(){

            //when && then
            Assertions.assertThrows(AlreadyMemberException::class.java) {
                groupDocument.addMember(userId = userId)
            }
        }
    }

    @Nested
    inner class RemoveMember{

        private val user1 = ObjectId.get()

        @BeforeEach
        fun setUp() {
            groupDocument = GroupDocument(
                name = UUID.randomUUID().toString(),
                members = mutableSetOf(userId, user1)
            )
        }

        @Test
        @DisplayName("존재하는 사용자를 제거")
        fun removeMemberFromExistMemberReturnSuccess(){
            //when
            groupDocument.removeMember(userId = userId)

            //then
            Assertions.assertFalse(groupDocument.members.contains(userId))
        }

        @Test
        @DisplayName("존재하지 않는 사용자를 제거")
        fun removeMemberFromNotExistMemberReturnSuccess(){
            //when
            Assertions.assertThrows(NotFoundMemberException::class.java) {
                groupDocument.removeMember(userId = ObjectId.get())
            }

            //then
            Assertions.assertEquals(2, groupDocument.members.size)
        }

        @Test
        @DisplayName("존재하는 사용자를 삭제하며, discussing 상태의 공유 일정에서 삭제")
        fun removeMemberThenRemoveMemberFromDiscussingScheduleReturnSuccess(){
            //given
            groupDocument.groupSchedules.add(
                GroupScheduleElement(
                    name = UUID.randomUUID().toString(),
                    startDate = UUID.randomUUID().toString(),
                    endDate = UUID.randomUUID().toString(),
                    startTime = UUID.randomUUID().toString(),
                    endTime = UUID.randomUUID().toString(),
                    scheduleMember = mutableSetOf(userId, user1)
                )
            )

            //when
            groupDocument.removeMember(userId = userId)

            //then
            Assertions.assertFalse(groupDocument.members.contains(userId))
            Assertions.assertFalse(groupDocument.groupSchedules[0].scheduleMember.contains(userId))
        }

        @Test
        @DisplayName("존재하는 사용자를 삭제하며, requested 상태의 공유 일정에서 삭제")
        fun removeMemberThenRemoveMemberFromRequestedScheduleReturnSuccess(){
            //given
            groupDocument.groupSchedules.add(
                GroupScheduleElement(
                    name = UUID.randomUUID().toString(),
                    startDate = UUID.randomUUID().toString(),
                    endDate = UUID.randomUUID().toString(),
                    startTime = UUID.randomUUID().toString(),
                    endTime = UUID.randomUUID().toString(),
                    scheduleMember = mutableSetOf(userId, user1),
                    state = GroupScheduleStateEnum.REQUESTED,
                    confirmedUser = mutableSetOf(userId)
                )
            )

            //when
            groupDocument.removeMember(userId = userId)

            //then
            Assertions.assertFalse(groupDocument.members.contains(userId))
            Assertions.assertFalse(groupDocument.groupSchedules[0].scheduleMember.contains(userId))
            Assertions.assertFalse(groupDocument.groupSchedules[0].confirmedUser.contains(userId))
        }

        @Test
        @DisplayName("존재하는 사용자를 삭제하며, rejected 상태의 공유 일정에서 삭제")
        fun removeMemberThenRemoveMemberFromRejectedScheduleReturnSuccess(){
            //given
            groupDocument.groupSchedules.add(
                GroupScheduleElement(
                    name = UUID.randomUUID().toString(),
                    startDate = UUID.randomUUID().toString(),
                    endDate = UUID.randomUUID().toString(),
                    startTime = UUID.randomUUID().toString(),
                    endTime = UUID.randomUUID().toString(),
                    scheduleMember = mutableSetOf(userId, user1),
                    state = GroupScheduleStateEnum.REJECTED
                )
            )

            //when
            groupDocument.removeMember(userId = userId)

            //then
            Assertions.assertFalse(groupDocument.members.contains(userId))
            Assertions.assertFalse(groupDocument.groupSchedules[0].scheduleMember.contains(userId))
        }

        @Test
        @DisplayName("존재하는 사용자를 삭제하며, confirmed 상태의 공유 일정에서는 스케줄 멤버에서만 삭제")
        fun removeMemberThenRemoveMemberFromConfirmedScheduleReturnSuccess(){
            //given
            groupDocument.groupSchedules.add(
                GroupScheduleElement(
                    name = UUID.randomUUID().toString(),
                    startDate = UUID.randomUUID().toString(),
                    endDate = UUID.randomUUID().toString(),
                    startTime = UUID.randomUUID().toString(),
                    endTime = UUID.randomUUID().toString(),
                    scheduleMember = mutableSetOf(userId, user1),
                    state = GroupScheduleStateEnum.CONFIRMED,
                    confirmedUser = mutableSetOf(userId, user1)
                )
            )

            //when
            groupDocument.removeMember(userId = userId)

            //then
            Assertions.assertFalse(groupDocument.members.contains(userId))
            Assertions.assertFalse(groupDocument.groupSchedules[0].scheduleMember.contains(userId))
            Assertions.assertTrue(groupDocument.groupSchedules[0].confirmedUser.contains(userId))
        }
    }

    @Nested
    inner class FindGroupScheduleById{
        @BeforeEach
        fun setUp() {
            groupDocument = GroupDocument(
                name = UUID.randomUUID().toString(),
            )
        }

        @Test
        @DisplayName("하나의 공유 일정 요소만 있는 리스트에서 해당 요소의 인덱스를 탐색")
        fun findGroupScheduleIndexByIdFromOneElementListReturnSuccess(){
            //given
            val groupScheduleElement = GroupScheduleElement(
                name = UUID.randomUUID().toString(),
                startDate = UUID.randomUUID().toString(),
                endDate = UUID.randomUUID().toString(),
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                scheduleMember = mutableSetOf(userId)
            )
            groupDocument.groupSchedules.add(groupScheduleElement)

            //when
            val result = groupDocument.findGroupScheduleById(groupScheduleId = groupScheduleElement.id)

            //then
            Assertions.assertEquals(groupScheduleElement, result)
        }

        @Test
        @DisplayName("존재하지 않는 공유 일정 요소의 인덱스를 탐색")
        fun findGroupScheduleIndexByIdFromEmptyListReturnException(){

            //when && then
            Assertions.assertThrows(NotFoundGroupScheduleException::class.java) {
                groupDocument.findGroupScheduleById(groupScheduleId = ObjectId.get())
            }
        }

        @Test
        @DisplayName("앞 뒤 20개씩 공유 일정 요소가 있는 공유 일정 요소의 인덱스를 탐색")
        fun findGroupScheduleIndexByIdFromBetween20ElementsListReturnSuccess(){
            //given
            for(i in 1..20){
                groupDocument.groupSchedules.add(GroupScheduleElement(
                    name = UUID.randomUUID().toString(),
                    startDate = UUID.randomUUID().toString(),
                    endDate = UUID.randomUUID().toString(),
                    startTime = UUID.randomUUID().toString(),
                    endTime = UUID.randomUUID().toString(),
                    scheduleMember = mutableSetOf(userId)
                ))
            }
            val groupScheduleElement = GroupScheduleElement(
                name = UUID.randomUUID().toString(),
                startDate = UUID.randomUUID().toString(),
                endDate = UUID.randomUUID().toString(),
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                scheduleMember = mutableSetOf(userId)
            )
            groupDocument.groupSchedules.add(groupScheduleElement)
            for(i in 1..20){
                groupDocument.groupSchedules.add(GroupScheduleElement(
                    name = UUID.randomUUID().toString(),
                    startDate = UUID.randomUUID().toString(),
                    endDate = UUID.randomUUID().toString(),
                    startTime = UUID.randomUUID().toString(),
                    endTime = UUID.randomUUID().toString(),
                    scheduleMember = mutableSetOf(userId)
                ))
            }

            //when
            val result = groupDocument.findGroupScheduleById(groupScheduleId = groupScheduleElement.id)

            //then
            Assertions.assertEquals(groupScheduleElement, result)
        }
    }

    @Nested
    inner class CreateGroupSchedule{
        @BeforeEach
        fun setUp() {
            groupDocument = GroupDocument(
                name = UUID.randomUUID().toString(),
                members = mutableSetOf(userId)
            )
        }

        @Test
        @DisplayName("빈 공유 일정 목록에 새로운 공유 일정을 생성")
        fun createGroupScheduleInEmptyListReturnSuccess(){
            //given
            val name = UUID.randomUUID().toString()
            val startDate = UUID.randomUUID().toString()
            val endDate = UUID.randomUUID().toString()
            val startTime = UUID.randomUUID().toString()
            val endTime = UUID.randomUUID().toString()

            //when
            val result = groupDocument.createGroupSchedule(
                name = name,
                startDate = startDate,
                endDate = endDate,
                startTime = startTime,
                endTime = endTime
            )

            //then
            Assertions.assertEquals(name, result.name)
            Assertions.assertEquals(startDate, result.startDate)
            Assertions.assertEquals(endDate, result.endDate)
            Assertions.assertEquals(startTime, result.startTime)
            Assertions.assertEquals(endTime, result.endTime)
            Assertions.assertEquals(result, groupDocument.groupSchedules.last())
            Assertions.assertTrue{
                groupDocument.groupSchedules[0].scheduleMember.contains(userId) &&
                        groupDocument.groupSchedules[0].scheduleMember.size == 1
            }
            Assertions.assertEquals(1, groupDocument.groupSchedules.size)
        }
    }

    @Nested
    inner class DeleteGroupScheduleElementById{
        @BeforeEach
        fun setUp() {
            groupDocument = GroupDocument(
                name = UUID.randomUUID().toString(),
            )
        }

        @Test
        @DisplayName("존재하는 공유 일정을 삭제 시도")
        fun deleteGroupScheduleElementByIdFromOneElementListReturnSuccess(){
            //given
            val groupScheduleElement = GroupScheduleElement(
                name = UUID.randomUUID().toString(),
                startDate = UUID.randomUUID().toString(),
                endDate = UUID.randomUUID().toString(),
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                scheduleMember = mutableSetOf(userId)
            )
            groupDocument.groupSchedules.add(groupScheduleElement)

            //when
            groupDocument.deleteGroupScheduleElementById(groupScheduleElement.id)

            //then
            Assertions.assertFalse(groupDocument.groupSchedules.contains(groupScheduleElement))
            Assertions.assertEquals(0, groupDocument.groupSchedules.size)
        }

        @Test
        @DisplayName("존재하지 않는 공유 일정을 삭제 시도")
        fun deleteGroupScheduleElementByIdFromEmptyListReturnSuccess(){
            //when && then
            Assertions.assertThrows(NotFoundGroupScheduleException::class.java) {
                groupDocument.deleteGroupScheduleElementById(ObjectId.get())
            }
        }
    }
}
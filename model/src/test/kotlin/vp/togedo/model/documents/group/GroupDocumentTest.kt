package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.group.AlreadyMemberException
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
}
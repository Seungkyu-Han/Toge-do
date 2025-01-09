package vp.togedo.model.documents.joinedGroup

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.joinedGroup.AlreadyJoinedGroupException
import vp.togedo.model.exception.joinedGroup.NotJoinedGroupException

class JoinedGroupDocumentTest{

    private val userId = ObjectId.get()

    private lateinit var joinedGroupDocument: JoinedGroupDocument

    @Nested
    inner class AddGroup{

        @BeforeEach
        fun setUp() {
            joinedGroupDocument = JoinedGroupDocument(
                id = userId
            )
        }

        @Test
        @DisplayName("새로운 그룹을 추가")
        fun addGroupReturnSuccess(){
            //given
            val groupId = ObjectId.get()

            //when
            joinedGroupDocument.addGroup(groupId)

            //then
            Assertions.assertTrue(joinedGroupDocument.groups.contains(groupId))
        }

        @Test
        @DisplayName("기존에 존재하는 그룹을 추가")
        fun addGroupExistGroupReturnException(){
            //given
            val groupId = ObjectId.get()
            joinedGroupDocument.groups.add(groupId)

            //when && then
            Assertions.assertThrows(AlreadyJoinedGroupException::class.java){
                joinedGroupDocument.addGroup(groupId)
            }
        }
    }

    @Nested
    inner class RemoveGroup{

        @BeforeEach
        fun setUp() {
            joinedGroupDocument = JoinedGroupDocument(
                id = userId
            )
        }

        @Test
        @DisplayName("존재하는 그룹을 삭제")
        fun removeGroupReturnSuccess(){
            //given
            val groupId = ObjectId.get()
            joinedGroupDocument.groups.add(groupId)

            //when
            joinedGroupDocument.removeGroup(groupId)

            //then
            Assertions.assertFalse(joinedGroupDocument.groups.contains(groupId))
        }

        @Test
        @DisplayName("존재하지 않는 그룹을 삭제")
        fun removeGroupNotExistReturnException(){

            //when && then
            Assertions.assertThrows(NotJoinedGroupException::class.java){
                joinedGroupDocument.removeGroup(ObjectId.get())
            }
        }
    }
}
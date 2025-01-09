package vp.togedo.model.documents.joinedGroup

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.joinedGroup.AlreadyJoinedGroupException

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

            //when
            Assertions.assertThrows(AlreadyJoinedGroupException::class.java){
                joinedGroupDocument.addGroup(groupId)
            }
        }
    }
}
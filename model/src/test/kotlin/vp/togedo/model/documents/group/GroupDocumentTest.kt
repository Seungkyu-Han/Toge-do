package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
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
}
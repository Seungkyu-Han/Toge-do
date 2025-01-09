package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.group.AlreadyMemberException

class IndividualScheduleDocumentTest{

    private lateinit var individualScheduleDocument: IndividualScheduleDocument

    private val userId = ObjectId.get()

    @Nested
    inner class AddMember{

        private val user1 = ObjectId.get()

        @BeforeEach
        fun setUp() {
            individualScheduleDocument = IndividualScheduleDocument(
                id = ObjectId.get(),
                mutableMapOf(
                    userId to IndividualScheduleList(),
                    user1 to IndividualScheduleList()
                )
            )
        }

        @Test
        @DisplayName("존재하지 않는 유저를 추가")
        fun addMemberNotExistMemberReturnSuccess(){
            //given
            val userId = ObjectId.get()

            //when
            individualScheduleDocument.addMember(userId)

            //then
            Assertions.assertTrue(individualScheduleDocument.individualScheduleMap.containsKey(userId))
        }

        @Test
        @DisplayName("존재하는 유저를 추가")
        fun addMemberExistMemberReturnSuccess(){
            //when && then
            Assertions.assertThrows(AlreadyMemberException::class.java) {
                individualScheduleDocument.addMember(userId)
            }
        }
    }
}
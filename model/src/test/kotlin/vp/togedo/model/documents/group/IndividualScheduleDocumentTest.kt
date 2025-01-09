package vp.togedo.model.documents.group

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import vp.togedo.model.exception.group.AlreadyMemberException
import vp.togedo.model.exception.group.NotFoundMemberException

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
        fun addMemberExistMemberReturnException(){
            //when && then
            Assertions.assertThrows(AlreadyMemberException::class.java) {
                individualScheduleDocument.addMember(userId)
            }
        }
    }

    @Nested
    inner class RemoveMember{
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
        @DisplayName("존재하는 유저를 삭제")
        fun removeMemberExistMemberReturnSuccess(){
            //given

            //when
            individualScheduleDocument.removeMember(userId)

            //then
            Assertions.assertFalse(individualScheduleDocument.individualScheduleMap.containsKey(userId))
        }

        @Test
        @DisplayName("존재하지 않는 유저를 삭제")
        fun removeMemberNotExistMemberReturnException(){
            //when && then
            Assertions.assertThrows(NotFoundMemberException::class.java) {
                individualScheduleDocument.removeMember(ObjectId.get())
            }
        }
    }

    @Nested
    inner class FindMemberById{
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
        @DisplayName("존재하는 유저를 조회")
        fun findMemberByIdReturnSuccess(){
            //when
            val result = individualScheduleDocument.findIndividualScheduleById(userId)

            Assertions.assertEquals(individualScheduleDocument.individualScheduleMap[userId], result)
        }

        @Test
        @DisplayName("존재하지 않는 유저를 조회")
        fun findMemberByIdReturnException(){
            Assertions.assertThrows(NotFoundMemberException::class.java) {
                individualScheduleDocument.findIndividualScheduleById(ObjectId.get())
            }
        }
    }
}
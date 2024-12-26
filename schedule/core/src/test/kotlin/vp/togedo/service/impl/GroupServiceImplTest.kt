package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.data.dao.GroupDao
import vp.togedo.data.dao.JoinedGroupDao
import vp.togedo.document.GroupDocument
import vp.togedo.document.JoinedGroupDocument
import vp.togedo.repository.GroupRepository
import vp.togedo.repository.JoinedGroupRepository
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.GroupException
import vp.togedo.util.error.exception.ScheduleException
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GroupServiceImpl::class])
class GroupServiceImplTest{

    @MockBean
    lateinit var groupRepository: GroupRepository

    @MockBean
    lateinit var joinedGroupRepository: JoinedGroupRepository

    private lateinit var groupServiceImpl: GroupServiceImpl

    @BeforeEach
    fun setUp() {
        groupServiceImpl = GroupServiceImpl(groupRepository, joinedGroupRepository)
    }

    @Nested
    inner class CreateGroup{

        private lateinit var userId:ObjectId

        private lateinit var name: String

        @BeforeEach
        fun setUp() {
            userId = ObjectId.get()
            name = UUID.randomUUID().toString()
        }

        @Test
        @DisplayName("2명의 사용자로 그룹을 생성")
        fun createGroupByTwoMemberReturnSuccess(){
            //given
            val members = mutableListOf(userId, ObjectId.get())
            val group = GroupDocument(
                id = ObjectId.get(),
                name = name,
                members = members.toMutableSet()
            )

            `when`(groupRepository.save(any()))
                .thenReturn(Mono.just(group))

            val expectedGroupDao = GroupDao(
                id = group.id,
                name = name,
                members = members
            )

            //when
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectNextMatches {
                    it == expectedGroupDao
                }.verifyComplete()

            //then
            verify(groupRepository, times(1)).save(any())
        }

        @Test
        @DisplayName("10명의 사용자로 그룹을 생성")
        fun createGroupByTenMemberReturnSuccess(){
            //given
            val members = mutableListOf(userId)
            for (i in 1..9){
                members.add(ObjectId.get())
            }
            val group = GroupDocument(
                id = ObjectId.get(),
                name = name,
                members = members.toMutableSet()
            )

            `when`(groupRepository.save(any()))
                .thenReturn(Mono.just(group))


            val expectedGroupDao = GroupDao(
                id = group.id,
                name = name,
                members = members
            )

            //when
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectNextMatches {
                    it == expectedGroupDao
                }.verifyComplete()

            //then
            verify(groupRepository, times(1)).save(any())
        }

        @Test
        @DisplayName("0명의 사용자로 그룹을 생성")
        fun createGroupByNoMemberReturnException(){
            //given
            val members = mutableListOf<ObjectId>()

            val group = GroupDocument(
                id = ObjectId.get(),
                name = name,
                members = members.toMutableSet()
            )

            `when`(groupRepository.save(any()))
                .thenReturn(Mono.just(group))

            //when
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.REQUIRE_MORE_MEMBER
                }.verify()

            //then
            verify(groupRepository, times(0)).save(any())
        }

        @Test
        @DisplayName("1명의 사용자로 그룹을 생성")
        fun createGroupByOneMemberReturnException(){
            //given
            val members = mutableListOf(userId)

            val group = GroupDocument(
                id = ObjectId.get(),
                name = name,
                members = members.toMutableSet()
            )

            `when`(groupRepository.save(any()))
                .thenReturn(Mono.just(group))

            //when
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.REQUIRE_MORE_MEMBER
                }.verify()

            //then
            verify(groupRepository, times(0)).save(any())
        }

        @Test
        @DisplayName("중복된 아이디로 2명이 그룹을 생성")
        fun createGroupByDuplicatedIdReturnException(){
            //given
            val members = mutableListOf(userId, userId)
            val group = GroupDocument(
                id = ObjectId.get(),
                name = name,
                members = members.toMutableSet()
            )

            `when`(groupRepository.save(any()))
                .thenReturn(Mono.just(group))

            //when && then
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.REQUIRE_MORE_MEMBER
                }.verify()

        }
    }

    @Nested
    inner class AddUserToGroup{

        private lateinit var group: GroupDocument

        @BeforeEach
        fun setUp() {
            group = GroupDocument(
                name = UUID.randomUUID().toString()
            )
        }

        @Test
        @DisplayName("2명인 그룹에 성공적으로 유저를 추가")
        fun addUserToGroupValidUserReturnSuccess(){
            //given
            val userId = ObjectId.get()
            group.members.add(ObjectId.get())
            group.members.add(ObjectId.get())

            `when`(groupRepository.findById(group.id)).thenReturn(Mono.just(group))
            `when`(groupRepository.save(group)).thenReturn(Mono.just(group))

            val expectedGroupDao = GroupDao(
                id = group.id,
                name = group.name,
                members = (group.members + userId).toList()
            )

            //when
            StepVerifier.create(groupServiceImpl.addUserToGroup(
                userId = userId, groupId = group.id
            )).expectNext(expectedGroupDao)
                .verifyComplete()

            //then
            Assertions.assertTrue(group.members.contains(userId))

            verify(groupRepository, times(1)).findById(group.id)
            verify(groupRepository, times(1)).save(group)
        }

        @Test
        @DisplayName("이미 포함되어 있는 그룹에 유저를 추가")
        fun addUserToAlreadyJoinedGroupReturnException(){
            //given
            val userId = ObjectId.get()
            group.members.add(ObjectId.get())
            group.members.add(userId)


            `when`(groupRepository.findById(group.id)).thenReturn(Mono.just(group))

            //when
            StepVerifier.create(groupServiceImpl.addUserToGroup(
                userId = userId, groupId = group.id
            )).expectErrorMatches {
                it is GroupException && it.errorCode == ErrorCode.ALREADY_JOINED_GROUP
            }.verify()

            //then
            Assertions.assertTrue(group.members.contains(userId))

            verify(groupRepository, times(1)).findById(group.id)
            verify(groupRepository, times(0)).save(group)
        }
    }

    @Nested
    inner class RemoveUserFromGroup{

        private lateinit var group: GroupDocument

        private lateinit var userId: ObjectId

        @BeforeEach
        fun setUp() {
            group = GroupDocument(
                name = UUID.randomUUID().toString()
            )

            userId = ObjectId.get()
        }

        @Test
        @DisplayName("2명 이상의 그룹에서 유저를 성공적으로 삭제하는 경우")
        fun removeUserFromTwoMemberGroupReturnSuccess(){
            //given
            group.members.add(userId)
            group.members.add(ObjectId.get())

            `when`(groupRepository.findById(group.id)).thenReturn(Mono.just(group))
            `when`(groupRepository.save(group)).thenReturn(Mono.just(group))

            val expectedGroupDao = GroupDao(
                id = group.id,
                name = group.name,
                members = (group.members - userId).toList()
            )

            //when
            StepVerifier.create(groupServiceImpl.removeUserFromGroup(
                groupId = group.id, userId = userId
            )).expectNextMatches {
                it == expectedGroupDao
            }.verifyComplete()

            //then
            Assertions.assertFalse(group.members.contains(userId))
            Assertions.assertEquals(1, group.members.size)

            verify(groupRepository, times(1)).findById(group.id)
            verify(groupRepository, times(1)).save(group)
        }

        @Test
        @DisplayName("1명인 그룹에서 유저가 탈퇴하여 그룹이 제거되는 경우")
        fun removeUserFromOneMemberGroupReturnSuccess(){
            //given
            group.members.add(userId)

            `when`(groupRepository.findById(group.id)).thenReturn(Mono.just(group))
            `when`(groupRepository.delete(group)).thenReturn(Mono.empty())

            val expectedGroupDao = GroupDao(
                id = group.id,
                name = group.name,
                members = (group.members - userId).toList()
            )

            //when
            StepVerifier.create(groupServiceImpl.removeUserFromGroup(
                groupId = group.id, userId = userId
            )).expectNextMatches {
                it == expectedGroupDao
            }.verifyComplete()

            //then
            Assertions.assertFalse(group.members.contains(userId))

            verify(groupRepository, times(1)).findById(group.id)
            verify(groupRepository, times(1)).delete(group)
        }

        @Test
        @DisplayName("유저가 속하지 않은 그룹에서 탈퇴를 시도")
        fun removeUserFromNotJoinedGroupReturnException(){
            //given
            group.members.add(ObjectId.get())
            group.members.add(ObjectId.get())

            `when`(groupRepository.findById(group.id)).thenReturn(Mono.just(group))
            `when`(groupRepository.delete(group)).thenReturn(Mono.empty())

            //when
            StepVerifier.create(groupServiceImpl.removeUserFromGroup(
                groupId = group.id, userId = userId
            )).expectErrorMatches {
                it is GroupException && it.errorCode == ErrorCode.NOT_JOINED_GROUP
            }.verify()

            //then
            Assertions.assertFalse(group.members.contains(userId))

            verify(groupRepository, times(1)).findById(group.id)
        }

        @Test
        @DisplayName("10명의 그룹에서 유효한 사용자가 탈퇴")
        fun removeUserFrom10MemberGroupReturnSuccess(){
            //given
            group.members.add(userId)
            for (i in 1..9)
                group.members.add(ObjectId.get())


            `when`(groupRepository.findById(group.id)).thenReturn(Mono.just(group))
            `when`(groupRepository.save(group)).thenReturn(Mono.just(group))

            val expectedGroupDao = GroupDao(
                id = group.id,
                name = group.name,
                members = (group.members - userId).toList()
            )

            //when
            StepVerifier.create(groupServiceImpl.removeUserFromGroup(
                groupId = group.id, userId = userId
            )).expectNextMatches {
                it == expectedGroupDao
            }.verifyComplete()

            //then
            Assertions.assertFalse(group.members.contains(userId))
            Assertions.assertEquals(9, group.members.size)

            verify(groupRepository, times(1)).findById(group.id)
            verify(groupRepository, times(1)).save(group)
        }
    }

    @Nested
    inner class AddGroupToJoinedGroup{

        private lateinit var groupId: ObjectId

        private lateinit var userId: ObjectId

        private lateinit var joinedGroup: JoinedGroupDocument

        @BeforeEach
        fun setUp() {
            groupId = ObjectId.get()
            userId = ObjectId.get()
            joinedGroup = JoinedGroupDocument(
                id = userId
            )

            `when`(joinedGroupRepository.findById(userId)).thenReturn(Mono.just(joinedGroup))
        }

        @Test
        @DisplayName("해당 유저의 joined group이 존재하고 다른 그룹이 속해있는 경우")
        fun addGroupToJoinedGroupAlreadyExistUserReturnSuccess(){
            //given
            joinedGroup.groups.add(ObjectId.get())


            `when`(joinedGroupRepository.save(joinedGroup))
                .thenReturn(Mono.just(joinedGroup))

            val expectedJoinedGroupDao = JoinedGroupDao(
                id = userId,
                groups = (joinedGroup.groups + groupId).toMutableSet()
            )

            //when
            StepVerifier.create(groupServiceImpl.addGroupToJoinedGroup(
                groupId = groupId, userId = userId
            )).expectNext(expectedJoinedGroupDao).verifyComplete()

            //then
            Assertions.assertTrue(joinedGroup.groups.contains(groupId))
            Assertions.assertEquals(2, joinedGroup.groups.size)
            verify(joinedGroupRepository, times(1)).findById(userId)
            verify(joinedGroupRepository, times(1)).save(joinedGroup)
        }

        @Test
        @DisplayName("해당 유저의 joined group이 존재하고 그룹은 존재하지 않는 경우")
        fun addGroupToJoinedGroupAlreadyEmptyJoinedGroupReturnSuccess(){
            //given

            `when`(joinedGroupRepository.save(joinedGroup))
                .thenReturn(Mono.just(joinedGroup))

            val expectedJoinedGroupDao = JoinedGroupDao(
                id = userId,
                groups = (joinedGroup.groups + groupId).toMutableSet()
            )

            //when
            StepVerifier.create(groupServiceImpl.addGroupToJoinedGroup(
                groupId = groupId, userId = userId
            )).expectNext(expectedJoinedGroupDao).verifyComplete()

            //then
            Assertions.assertTrue(joinedGroup.groups.contains(groupId))
            Assertions.assertEquals(1, joinedGroup.groups.size)
            verify(joinedGroupRepository, times(1)).findById(userId)
            verify(joinedGroupRepository, times(1)).save(joinedGroup)
        }
    }
}
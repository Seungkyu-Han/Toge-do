package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.data.dao.GroupDao
import vp.togedo.document.GroupDocument
import vp.togedo.repository.GroupRepository
import vp.togedo.repository.JoinedGroupRepository
import vp.togedo.util.error.errorCode.ErrorCode
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

            //when && then
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectNextMatches {
                    it == expectedGroupDao
                }.verifyComplete()
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

            //when && then
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectNextMatches {
                    it == expectedGroupDao
                }.verifyComplete()
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

            //when && then
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.REQUIRE_MORE_MEMBER
                }.verify()
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

            //when && then
            StepVerifier.create(groupServiceImpl.createGroup(name, members))
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.REQUIRE_MORE_MEMBER
                }.verify()
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
}
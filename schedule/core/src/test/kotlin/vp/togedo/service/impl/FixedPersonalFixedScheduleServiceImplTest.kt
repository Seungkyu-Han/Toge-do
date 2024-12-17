package vp.togedo.service.impl

import kotlinx.coroutines.reactor.mono
import org.bson.types.ObjectId
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.data.dao.FixedScheduleDao
import vp.togedo.document.FixedPersonalScheduleDocument
import vp.togedo.document.FixedSchedule
import vp.togedo.repository.FixedPersonalScheduleRepository
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.ScheduleException
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [FixedPersonalScheduleServiceImpl::class])
class FixedPersonalFixedScheduleServiceImplTest{

    @MockBean
    private lateinit var fixedPersonalScheduleRepository: FixedPersonalScheduleRepository

    private lateinit var fixedPersonalScheduleService: FixedPersonalScheduleServiceImpl

    @BeforeEach
    fun setUp() {
        fixedPersonalScheduleService = FixedPersonalScheduleServiceImpl(
            fixedPersonalScheduleRepository = fixedPersonalScheduleRepository
        )
    }

    @Nested
    inner class CreateFixedSchedule{

        private val userId = ObjectId.get()

        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = userId,
            fixedSchedules = mutableListOf()
        )

        @Test
        @DisplayName("해당 유저의 스케줄이 없는 경우")
        fun createScheduleWhenNotExistScheduleReturnSuccess(){
            //given
            val fixedScheduleDaoList = mutableListOf(
                FixedScheduleDao(
                    scheduleId = null,
                    startTime = 11100,
                    endTime = 11200,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )
            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.empty())

            `when`(fixedPersonalScheduleRepository.save(any<FixedPersonalScheduleDocument>()))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.createSchedule(userId, fixedScheduleDaoList)
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    fixedScheduleDaoList[0].startTime == it[0].startTime &&
                            fixedScheduleDaoList[0].endTime == it[0].endTime &&
                            fixedScheduleDaoList[0].title == it[0].title &&
                            fixedScheduleDaoList[0].color == it[0].color &&
                            it[0].scheduleId != null
                }.verifyComplete()
        }

        @Test
        @DisplayName("해당 유저의 스케줄이 존재하는 경우")
        fun createScheduleWhenExistScheduleReturnSuccess(){
            //given
            val fixedScheduleDaoList = mutableListOf(
                FixedScheduleDao(
                    scheduleId = null,
                    startTime = 11100,
                    endTime = 11200,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.createSchedule(userId, fixedScheduleDaoList)
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    fixedScheduleDaoList[0].startTime == it[0].startTime &&
                            fixedScheduleDaoList[0].endTime == it[0].endTime &&
                            fixedScheduleDaoList[0].title == it[0].title &&
                            fixedScheduleDaoList[0].color == it[0].color &&
                            it[0].scheduleId != null
                }.verifyComplete()
        }

        @Test
        @DisplayName("조회한 스케줄에 일정이 존재하는 경우")
        fun createScheduleWhenExistScheduleHaveSchedulesReturnSuccess(){
            //given
            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = ObjectId.get(),
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val fixedScheduleDaoList = mutableListOf(
                FixedScheduleDao(
                    scheduleId = null,
                    startTime = 11200,
                    endTime = 11259,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.createSchedule(userId, fixedScheduleDaoList)
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    fixedScheduleDaoList[0].startTime == it[0].startTime &&
                            fixedScheduleDaoList[0].endTime == it[0].endTime &&
                            fixedScheduleDaoList[0].title == it[0].title &&
                            fixedScheduleDaoList[0].color == it[0].color &&
                            it[0].scheduleId != null
                }.verifyComplete()
        }

        @Test
        @DisplayName("기존에 존재하는 일정과 충돌하는 경우")
        fun createScheduleWhenExistAndConflictReturnException(){
            //given
            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = ObjectId.get(),
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )


            val fixedScheduleDaoList = mutableListOf(
                FixedScheduleDao(
                    scheduleId = null,
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.createSchedule(userId, fixedScheduleDaoList)
            }

            //then
            StepVerifier.create(coroutine)
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.SCHEDULE_CONFLICT
                }
                .verify()
        }

        @Test
        @DisplayName("추가하려는 스케줄끼리 충돌하는 경우")
        fun createScheduleWhenExistAndConflictWithInsertSchedulesReturnException(){
            //given

            val fixedScheduleDaoLists = mutableListOf(
                FixedScheduleDao(
                    scheduleId = null,
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ),
                FixedScheduleDao(
                    scheduleId = null,
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.createSchedule(userId, fixedScheduleDaoLists)
            }

            //then
            StepVerifier.create(coroutine)
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.SCHEDULE_CONFLICT
                }
                .verify()
        }
    }

    @Nested
    inner class ReadFixedSchedule{

        private val userId = ObjectId.get()

        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = userId,
            fixedSchedules = mutableListOf()
        )

        @Test
        @DisplayName("스케줄이 존재하지 않는 유저가 스케줄을 조회")
        fun readScheduleByNotExistScheduleUserReturnSuccess(){
            //given
            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.empty())

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.readSchedule(userId)
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    it.isEmpty()
                }.verifyComplete()
        }

        @Test
        @DisplayName("스케줄이 하나 존재하는 유저가 스케줄을 조회")
        fun readScheduleByExistOneScheduleUserReturnSuccess(){
            //given
            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = ObjectId.get(),
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.readSchedule(userId)
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    it.size == fixedPersonalScheduleDocument.fixedSchedules.size &&
                            it[0].scheduleId == fixedPersonalScheduleDocument.fixedSchedules[0].id &&
                            it[0].title == fixedPersonalScheduleDocument.fixedSchedules[0].title &&
                            it[0].color == fixedPersonalScheduleDocument.fixedSchedules[0].color &&
                            it[0].startTime == fixedPersonalScheduleDocument.fixedSchedules[0].startTime &&
                            it[0].endTime == fixedPersonalScheduleDocument.fixedSchedules[0].endTime
                }.verifyComplete()
        }

        @Test
        @DisplayName("스케줄이 두개 이상 존재하는 유저가 스케줄을 조회")
        fun readScheduleByExistMoreThanOneScheduleUserReturnSuccess(){
            //given
            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = ObjectId.get(),
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = ObjectId.get(),
                    startTime = 11200,
                    endTime = 11259,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.readSchedule(userId)
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    it.size == fixedPersonalScheduleDocument.fixedSchedules.size &&
                            it[0].scheduleId == fixedPersonalScheduleDocument.fixedSchedules[0].id &&
                            it[0].title == fixedPersonalScheduleDocument.fixedSchedules[0].title &&
                            it[0].color == fixedPersonalScheduleDocument.fixedSchedules[0].color &&
                            it[0].startTime == fixedPersonalScheduleDocument.fixedSchedules[0].startTime &&
                            it[0].endTime == fixedPersonalScheduleDocument.fixedSchedules[0].endTime &&

                            it[1].scheduleId == fixedPersonalScheduleDocument.fixedSchedules[1].id &&
                            it[1].title == fixedPersonalScheduleDocument.fixedSchedules[1].title &&
                            it[1].color == fixedPersonalScheduleDocument.fixedSchedules[1].color &&
                            it[1].startTime == fixedPersonalScheduleDocument.fixedSchedules[1].startTime &&
                            it[1].endTime == fixedPersonalScheduleDocument.fixedSchedules[1].endTime
                }.verifyComplete()
        }
    }

    @Nested
    inner class ModifyFixedSchedule{
        private val userId = ObjectId.get()
        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = userId,
            fixedSchedules = mutableListOf()
        )

        @Test
        @DisplayName("하나의 유효한 스케줄을 수정 시도")
        fun modifyToExistScheduleOneElementReturnSuccess(){
            //given
            val scheduleId = ObjectId.get()
            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = scheduleId,
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val fixedScheduleDaoList = mutableListOf(
                FixedScheduleDao(
                    scheduleId = scheduleId,
                    startTime = 11200,
                    endTime = 11259,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.modifySchedule(
                    userId = userId,
                    fixedScheduleDaoList = fixedScheduleDaoList
                )
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    it.size == fixedScheduleDaoList.size &&
                            it[0].scheduleId == scheduleId &&
                            it[0].startTime == fixedScheduleDaoList[0].startTime &&
                            it[0].endTime == fixedScheduleDaoList[0].endTime &&
                            it[0].title == fixedScheduleDaoList[0].title &&
                            it[0].color == fixedScheduleDaoList[0].color
                }.verifyComplete()
        }

        @Test
        @DisplayName("두개 이상의 유효한 스케줄을 수정 시도")
        fun modifyToExistScheduleMoreThanElementReturnSuccess(){
            //given
            val scheduleIdList = listOf(ObjectId.get(), ObjectId.get())
            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = scheduleIdList[0],
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = scheduleIdList[1],
                    startTime = 21100,
                    endTime = 21159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            val fixedScheduleDaoLists = mutableListOf(
                FixedScheduleDao(
                    scheduleId = scheduleIdList[1],
                    startTime = 31100,
                    endTime = 31159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                ),
                FixedScheduleDao(
                    scheduleId = scheduleIdList[0],
                    startTime = 21100,
                    endTime = 21159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.modifySchedule(
                    userId = userId,
                    fixedScheduleDaoList = fixedScheduleDaoLists
                )
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextMatches {
                    it.size == fixedScheduleDaoLists.size &&
                            it[0].scheduleId == fixedScheduleDaoLists[0].scheduleId &&
                            it[0].startTime == fixedScheduleDaoLists[0].startTime &&
                            it[0].endTime == fixedScheduleDaoLists[0].endTime &&
                            it[0].title == fixedScheduleDaoLists[0].title &&
                            it[0].color == fixedScheduleDaoLists[0].color &&

                            it[1].scheduleId == fixedScheduleDaoLists[1].scheduleId &&
                            it[1].startTime == fixedScheduleDaoLists[1].startTime &&
                            it[1].endTime == fixedScheduleDaoLists[1].endTime &&
                            it[1].title == fixedScheduleDaoLists[1].title &&
                            it[1].color == fixedScheduleDaoLists[1].color
                }.verifyComplete()
        }

        @Test
        @DisplayName("유효하지 않은 아이디로 스케줄을 수정하려고 시도")
        fun modifyToExistScheduleByInvalidIdReturnException(){
            //given
            val fixedScheduleDaoList = mutableListOf(
                FixedScheduleDao(
                    scheduleId = ObjectId.get(),
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.modifySchedule(
                    userId = userId,
                    fixedScheduleDaoList = fixedScheduleDaoList
                )
            }

            //then
            StepVerifier.create(coroutine)
                .expectErrorMatches{
                    it is ScheduleException && it.errorCode == ErrorCode.SCHEDULE_NOT_FOUND
                }
                .verify()
        }

        @Test
        @DisplayName("스케줄을 만든 적이 없는 유저가 스케줄을 수정하려고 시도")
        fun modifyToNotExistScheduleReturnException(){
            //given
            val fixedScheduleDaoList = mutableListOf(
                FixedScheduleDao(
                    scheduleId = ObjectId.get(),
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.empty())

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.modifySchedule(
                    userId = userId,
                    fixedScheduleDaoList = fixedScheduleDaoList
                )
            }

            //then
            StepVerifier.create(coroutine)
                .expectErrorMatches{
                    it is ScheduleException && it.errorCode == ErrorCode.SCHEDULE_INFO_CANT_FIND
                }
                .verify()
        }
    }

    @Nested
    inner class DeleteFixedSchedule{
        private val userId = ObjectId.get()

        private val fixedPersonalScheduleDocument = FixedPersonalScheduleDocument(
            id = ObjectId.get(),
            userId = userId,
            fixedSchedules = mutableListOf()
        )

        @Test
        @DisplayName("존재하는 스케줄을 삭제 시도")
        fun deleteScheduleToExistScheduleReturnSuccess(){
            //given
            val scheduleId = ObjectId.get()

            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = scheduleId,
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.deleteSchedule(userId, listOf(scheduleId))
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextCount(1)
                .verifyComplete()

            Assertions.assertTrue{
                fixedPersonalScheduleDocument.fixedSchedules.find{
                    it.id == scheduleId
                } == null
            }
        }

        @Test
        @DisplayName("2개 이상의 스케줄을 삭제")
        fun deleteScheduleToMoreThanOneElementScheduleReturnSuccess(){
            //given
            val scheduleIdList = listOf(ObjectId.get(), ObjectId.get())

            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = scheduleIdList[0],
                    startTime = 11100,
                    endTime = 11159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            fixedPersonalScheduleDocument.fixedSchedules.add(
                FixedSchedule(
                    id = scheduleIdList[1],
                    startTime = 21100,
                    endTime = 21159,
                    title = UUID.randomUUID().toString(),
                    color = UUID.randomUUID().toString()
                )
            )

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            `when`(fixedPersonalScheduleRepository.save(fixedPersonalScheduleDocument))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.deleteSchedule(userId, scheduleIdList)
            }

            //then
            StepVerifier.create(coroutine)
                .expectNextCount(1)
                .verifyComplete()

            Assertions.assertTrue{
                fixedPersonalScheduleDocument.fixedSchedules.find{
                    it.id == scheduleIdList[0]
                } == null
            }

            Assertions.assertTrue{
                fixedPersonalScheduleDocument.fixedSchedules.find{
                    it.id == scheduleIdList[1]
                } == null
            }
        }

        @Test
        @DisplayName("해당 유저의 스케줄이 존재하지 않는 경우")
        fun deleteScheduleToNotExistScheduleInfoReturnException(){
            //given
            val scheduleId = ObjectId.get()
            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.empty())

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.deleteSchedule(userId, listOf(scheduleId))
            }

            //then
            StepVerifier.create(coroutine)
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.SCHEDULE_INFO_CANT_FIND
                }.verify()
        }

        @Test
        @DisplayName("존재하지 않는 스케줄을 삭제 시도")
        fun deleteScheduleToNotExistScheduleReturnException(){
            //given
            val scheduleId = ObjectId.get()

            `when`(fixedPersonalScheduleRepository.findByUserId(userId))
                .thenReturn(Mono.just(fixedPersonalScheduleDocument))

            //when
            val coroutine = mono{
                fixedPersonalScheduleService.deleteSchedule(userId, listOf(scheduleId))
            }

            //then
            StepVerifier.create(coroutine)
                .expectErrorMatches {
                    it is ScheduleException && it.errorCode == ErrorCode.SCHEDULE_NOT_FOUND
                }.verify()
        }


    }
}
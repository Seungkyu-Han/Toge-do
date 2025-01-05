package vp.togedo.kafka.service.impl

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.data.groupSchedule.ConfirmScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.CreateGroupScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.SuggestGroupScheduleEventDto
import vp.togedo.kafka.publish.KafkaPublisher
import vp.togedo.kafka.service.GroupScheduleKafkaService
import java.util.UUID

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GroupScheduleKafkaServiceImpl::class])
class GroupScheduleKafkaServiceImplTest{

    @MockBean
    private lateinit var kafkaPublisher: KafkaPublisher

    private lateinit var groupScheduleKafkaService: GroupScheduleKafkaService

    @BeforeEach
    fun setup() {
        groupScheduleKafkaService = GroupScheduleKafkaServiceImpl(kafkaPublisher)
    }

    @Nested
    inner class PublishSuggestConfirmScheduleEvent{
        private val suggestGroupScheduleEventDto = SuggestGroupScheduleEventDto(
            receiverId = UUID.randomUUID().toString(),
            name = UUID.randomUUID().toString()
        )

        @Test
        @DisplayName("일정 확인을 요청하는 kafka 이벤트를 발행")
        fun suggestConfirmScheduleEventReturnSuccess(){
            //given
            `when`(kafkaPublisher.publishKafkaEvent(
                EventEnums.SUGGEST_CONFIRM_SCHEDULE,
                suggestGroupScheduleEventDto
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(groupScheduleKafkaService.publishSuggestConfirmScheduleEvent(suggestGroupScheduleEventDto))
                .verifyComplete()

            //then
            verify(kafkaPublisher, times(1)).publishKafkaEvent(
                EventEnums.SUGGEST_CONFIRM_SCHEDULE,
                suggestGroupScheduleEventDto
            )
        }
    }

    @Nested
    inner class PublishConfirmScheduleEvent{
        private val confirmScheduleEventDto = ConfirmScheduleEventDto(
            receiverId = UUID.randomUUID().toString(),
            name = UUID.randomUUID().toString()
        )

        @Test
        @DisplayName("일정을 확인하는 kafka 이벤트를 발행")
        fun confirmScheduleEventReturnSuccess(){
            //given
            `when`(kafkaPublisher.publishKafkaEvent(
                EventEnums.CONFIRM_SCHEDULE,
                confirmScheduleEventDto
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(groupScheduleKafkaService.publishConfirmScheduleEvent(confirmScheduleEventDto))
                .verifyComplete()

            //then
            verify(kafkaPublisher, times(1)).publishKafkaEvent(
                EventEnums.CONFIRM_SCHEDULE,
                confirmScheduleEventDto
            )
        }
    }

    @Nested
    inner class PublishCreateGroupScheduleEvent{
        private val createGroupScheduleEventDto = CreateGroupScheduleEventDto(
            receiverId = UUID.randomUUID().toString(),
            name = UUID.randomUUID().toString()
        )

        @Test
        @DisplayName("공유 일정을 생성하는 kafka 이벤트를 발행")
        fun createGroupScheduleEventReturnSuccess(){
            //given
            `when`(kafkaPublisher.publishKafkaEvent(
                EventEnums.CREATE_GROUP_SCHEDULE,
                createGroupScheduleEventDto
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(groupScheduleKafkaService.publishCreateGroupScheduleEvent(createGroupScheduleEventDto))
                .verifyComplete()

            //then
            verify(kafkaPublisher, times(1)).publishKafkaEvent(
                EventEnums.CREATE_GROUP_SCHEDULE,
                createGroupScheduleEventDto
            )
        }
    }

}
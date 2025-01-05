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
import vp.togedo.kafka.data.group.InviteGroupEventDto
import vp.togedo.kafka.publish.KafkaPublisher
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GroupKafkaServiceImpl::class])
class GroupKafkaServiceImplTest{

    @MockBean
    private lateinit var kafkaPublisher: KafkaPublisher

    private lateinit var groupKafkaService: GroupKafkaServiceImpl

    @BeforeEach
    fun setup() {
        groupKafkaService = GroupKafkaServiceImpl(kafkaPublisher)
    }

    @Nested
    inner class PublishInviteGroupEvent {

        private val inviteGroupEventDto = InviteGroupEventDto(
            receiverId = UUID.randomUUID().toString(),
            name = UUID.randomUUID().toString()
        )

        @Test
        @DisplayName("그룹 초대 이벤트를 전송하는 kafka 이벤트를 발행")
        fun inviteGroupEventReturnSuccess(){
            //given
            `when`(kafkaPublisher.publishKafkaEvent(
                EventEnums.INVITE_GROUP,
                inviteGroupEventDto
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(groupKafkaService.publishInviteGroupEvent(inviteGroupEventDto))
                .verifyComplete()

            //then
            verify(kafkaPublisher, times(1)).publishKafkaEvent(
                EventEnums.INVITE_GROUP,
                inviteGroupEventDto
            )
        }


    }
}
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
import vp.togedo.kafka.data.friend.FriendApproveEventDto
import vp.togedo.kafka.data.friend.FriendRequestEventDto
import vp.togedo.kafka.publish.KafkaPublisher
import java.util.*


@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [FriendKafkaServiceImpl::class])
class FriendKafkaServiceImplTest{

    @MockBean
    private lateinit var kafkaPublisher: KafkaPublisher

    private lateinit var friendKafkaService: FriendKafkaServiceImpl

    @BeforeEach
    fun setup() {
        friendKafkaService = FriendKafkaServiceImpl(kafkaPublisher)
    }


    @Nested
    inner class PublishRequestFriendEvent{
        private val friendRequestEventDto = FriendRequestEventDto(
            receiverId = UUID.randomUUID().toString(),
            sender = UUID.randomUUID().toString(),
            image = UUID.randomUUID().toString()
        )

        @Test
        @DisplayName("친구 요청을 보내는 kafka 이벤트를 발행")
        fun requestFriendEventReturnSuccess(){
            //given
            `when`(kafkaPublisher.publishKafkaEvent(
                EventEnums.REQUEST_FRIEND,
                friendRequestEventDto
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(friendKafkaService.publishRequestFriendEvent(friendRequestEventDto))
                .verifyComplete()

            //then
            verify(kafkaPublisher, times(1)).publishKafkaEvent(
                EventEnums.REQUEST_FRIEND,
                friendRequestEventDto
            )
        }
    }

    @Nested
    inner class PublishApproveFriendEvent{
        private val friendApproveEventDto = FriendApproveEventDto(
            receiverId = UUID.randomUUID().toString(),
            sender = UUID.randomUUID().toString(),
            image = UUID.randomUUID().toString()
        )

        @Test
        @DisplayName("친구 요청 승인 보내는 kafka 이벤트를 발행")
        fun approveFriendEventReturnSuccess(){
            //given
            `when`(kafkaPublisher.publishKafkaEvent(
                EventEnums.APPROVE_FRIEND,
                friendApproveEventDto
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(friendKafkaService.publishApproveFriendEvent(friendApproveEventDto))
                .verifyComplete()

            //then
            verify(kafkaPublisher, times(1)).publishKafkaEvent(
                EventEnums.APPROVE_FRIEND,
                friendApproveEventDto
            )
        }
    }
}
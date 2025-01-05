package vp.togedo.kafka.publish.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.data.group.InviteGroupEventDto
import vp.togedo.kafka.publish.KafkaPublisher
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [KafkaPublisherImpl::class])
class KafkaPublisherImplTest{

    @MockBean
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>

    private lateinit var kafkaPublisher: KafkaPublisher

    @BeforeEach
    fun setup() {
        kafkaPublisher = KafkaPublisherImpl(objectMapper, reactiveKafkaProducerTemplate)
    }

    @Nested
    inner class PublishKafkaEvent{
        private val eventEnum = EventEnums.INVITE_GROUP

        private val eventClass = InviteGroupEventDto(
            receiverId = UUID.randomUUID().toString(),
            name = UUID.randomUUID().toString()
        )

        @Test
        @DisplayName("kafka 이벤트 발행을 테스트")
        fun publishKafkaEventReturnSuccess(){
            //given
            `when`(objectMapper.writeValueAsString(eventClass)).thenReturn(eventClass.toString())
            `when`(reactiveKafkaProducerTemplate.send(eventEnum.topics, eventClass.toString()))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(kafkaPublisher.publishKafkaEvent(
                eventEnum, eventClass
            )).verifyComplete()

            //then
            verify(objectMapper, times(1)).writeValueAsString(eventClass)
            verify(reactiveKafkaProducerTemplate, times(1)).send(eventEnum.topics, eventClass.toString())
        }
    }
}
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
import vp.togedo.kafka.data.email.ValidCodeEventDto
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.publish.KafkaPublisher
import vp.togedo.kafka.service.EmailKafkaService

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [EmailKafkaServiceImpl::class])
class EmailKafkaServiceImplTest{

    @MockBean
    private lateinit var kafkaPublisher: KafkaPublisher

    private lateinit var emailKafkaService: EmailKafkaService

    @BeforeEach
    fun setup() {
        emailKafkaService = EmailKafkaServiceImpl(kafkaPublisher)
    }

    @Nested
    inner class PublishSendValidCodeEvent{
        private val validCodeEventDto = ValidCodeEventDto(
            code = "valid-code-event-code",
            email = "test@example.com"
        )

        @Test
        @DisplayName("인증번호 이메일을 전송하는 kafka 이벤트를 발행")
        fun sendValidCodeEventReturnSuccess(){
            //given
            `when`(kafkaPublisher.publishKafkaEvent(
                EventEnums.SEND_VALID_CODE_EVENT,
                validCodeEventDto
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(emailKafkaService.publishSendValidCodeEvent(validCodeEventDto))
                .verifyComplete()

            //then
            verify(kafkaPublisher, times(1)).publishKafkaEvent(
                EventEnums.SEND_VALID_CODE_EVENT,
                validCodeEventDto
            )
        }
    }

}
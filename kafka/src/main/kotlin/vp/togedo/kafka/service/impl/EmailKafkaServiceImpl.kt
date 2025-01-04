package vp.togedo.kafka.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.kafka.data.email.ValidCodeEventDto
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.publish.KafkaPublisher
import vp.togedo.kafka.service.EmailKafkaService

@Service
class EmailKafkaServiceImpl(
    private val kafkaPublisher: KafkaPublisher
): EmailKafkaService {

    override fun publishSendValidCodeEvent(validCodeEventDto: ValidCodeEventDto): Mono<Void> {
        return kafkaPublisher.publishKafkaEvent(
            EventEnums.SEND_VALID_CODE_EVENT,
            validCodeEventDto
        )
    }
}
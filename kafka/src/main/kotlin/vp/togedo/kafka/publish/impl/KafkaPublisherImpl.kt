package vp.togedo.kafka.publish.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.publish.KafkaPublisher

@Service
class KafkaPublisherImpl(
    private val objectMapper: ObjectMapper,
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>
): KafkaPublisher {

    override fun publishKafkaEvent(eventEnums: EventEnums, eventClass: Any): Mono<Void> {
        return reactiveKafkaProducerTemplate.send(
            eventEnums.topics,
            objectMapper.writeValueAsString(
                eventClass
            )
        ).then()
    }
}
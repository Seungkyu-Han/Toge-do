package vp.togedo.kafka.publish

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.enums.EventEnums

interface KafkaPublisher {

    fun publishKafkaEvent(eventEnums: EventEnums, eventClass: Any): Mono<Void>
}
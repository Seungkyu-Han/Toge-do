package vp.togedo.kafka.service

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.email.ValidCodeEventDto

interface EmailKafkaService {

    fun publishSendValidCodeEvent(validCodeEventDto: ValidCodeEventDto): Mono<Void>
}
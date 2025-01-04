package vp.togedo.kafka.service

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.group.InviteGroupEventDto

interface GroupKafkaService {

    fun publishInviteGroupEvent(inviteGroupEventDto: InviteGroupEventDto): Mono<Void>
}
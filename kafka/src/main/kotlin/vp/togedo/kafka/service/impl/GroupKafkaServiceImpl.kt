package vp.togedo.kafka.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.data.group.InviteGroupEventDto
import vp.togedo.kafka.publish.KafkaPublisher
import vp.togedo.kafka.service.GroupKafkaService

@Service
class GroupKafkaServiceImpl(
    private val kafkaPublisher: KafkaPublisher
): GroupKafkaService {

    override fun publishInviteGroupEvent(inviteGroupEventDto: InviteGroupEventDto): Mono<Void> {
        return kafkaPublisher.publishKafkaEvent(
            EventEnums.INVITE_GROUP,
            inviteGroupEventDto
        )
    }
}
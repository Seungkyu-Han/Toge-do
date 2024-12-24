package vp.togedo.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.data.dto.group.InviteGroupEventDto
import vp.togedo.document.GroupDocument
import vp.togedo.service.KafkaService

@Service
class KafkaServiceImpl(
    private val objectMapper: ObjectMapper,
    private val reactiveKafkaProducerTemplate: ReactiveKafkaProducerTemplate<String, String>
): KafkaService {

    private val inviteGroupTopic = "INVITE_GROUP_TOPIC"

    override fun publishInviteGroupEvent(receiverId: ObjectId, group: GroupDocument): Mono<SenderResult<Void>> =
        reactiveKafkaProducerTemplate.send(
            inviteGroupTopic,
            objectMapper.writeValueAsString(
                InviteGroupEventDto(
                    receiverId = receiverId.toString(),
                    name = group.name
                )
            )
        )

}
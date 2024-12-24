package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult
import vp.togedo.document.GroupDocument

interface KafkaService {

    fun publishInviteGroupEvent(receiverId: ObjectId, group: GroupDocument): Mono<SenderResult<Void>>
}
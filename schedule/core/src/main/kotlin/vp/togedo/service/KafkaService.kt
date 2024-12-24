package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.data.dao.GroupDao

interface KafkaService {

    fun publishInviteGroupEvent(receiverId: ObjectId, group: GroupDao): Mono<Void>
}
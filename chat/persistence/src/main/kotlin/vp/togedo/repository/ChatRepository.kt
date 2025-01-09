package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import vp.togedo.model.documents.chat.ChatDocument

interface ChatRepository: ReactiveMongoRepository<ChatDocument, ObjectId> {

    fun findByGroupId(groupId: ObjectId): Flux<ChatDocument>
}
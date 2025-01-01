package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import vp.togedo.document.ChatDocument

interface ChatRepository: ReactiveMongoRepository<ChatDocument, ObjectId> {
}
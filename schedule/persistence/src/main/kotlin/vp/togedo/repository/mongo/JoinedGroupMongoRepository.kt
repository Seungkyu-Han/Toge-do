package vp.togedo.repository.mongo

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import vp.togedo.document.JoinedGroupDocument

interface JoinedGroupMongoRepository: ReactiveMongoRepository<JoinedGroupDocument, ObjectId> {
}
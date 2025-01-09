package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import vp.togedo.model.documents.group.GroupDocument

interface GroupRepository: ReactiveMongoRepository<GroupDocument, ObjectId> {
}
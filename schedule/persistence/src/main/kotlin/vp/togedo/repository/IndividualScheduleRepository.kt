package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import vp.togedo.model.documents.group.IndividualScheduleDocument

interface IndividualScheduleRepository: ReactiveMongoRepository<IndividualScheduleDocument, ObjectId> {
}
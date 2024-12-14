package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import vp.togedo.document.FixedPersonalScheduleDocument

interface FixedPersonalScheduleRepository: ReactiveMongoRepository<FixedPersonalScheduleDocument, ObjectId>{

}
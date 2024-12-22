package vp.togedo.repository.mongo

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import vp.togedo.document.PersonalScheduleDocument

interface PersonalScheduleMongoRepository: ReactiveMongoRepository<PersonalScheduleDocument, ObjectId> {

    fun findByUserId(userId: ObjectId): Mono<PersonalScheduleDocument>
}
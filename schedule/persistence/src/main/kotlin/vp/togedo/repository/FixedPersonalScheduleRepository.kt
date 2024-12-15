package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import vp.togedo.document.FixedPersonalScheduleDocument

interface FixedPersonalScheduleRepository: ReactiveMongoRepository<FixedPersonalScheduleDocument, ObjectId>{

    fun findByUserId(userId: ObjectId): Mono<FixedPersonalScheduleDocument>

}
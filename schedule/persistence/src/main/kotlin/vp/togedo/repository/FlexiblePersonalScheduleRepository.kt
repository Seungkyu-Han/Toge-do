package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import vp.togedo.document.FlexiblePersonalScheduleDocument

interface FlexiblePersonalScheduleRepository: ReactiveMongoRepository<FlexiblePersonalScheduleDocument, ObjectId> {

    fun findByUserId(userId: ObjectId): Mono<FlexiblePersonalScheduleDocument>
}
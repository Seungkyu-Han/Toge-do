package vp.togedo.redis.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.model.documents.personalSchedule.PersonalScheduleDocument

interface PersonalScheduleRedisRepository {

    fun findById(id: ObjectId): Mono<PersonalScheduleDocument>

    fun save(personalScheduleDocument: PersonalScheduleDocument): Mono<Void>
}
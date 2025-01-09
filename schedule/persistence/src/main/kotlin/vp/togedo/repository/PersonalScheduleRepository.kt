package vp.togedo.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.model.documents.personalSchedule.PersonalScheduleDocument

interface PersonalScheduleRepository {

    fun findByUserId(userId: ObjectId): Mono<PersonalScheduleDocument>

    fun save(personalScheduleDocument: PersonalScheduleDocument): Mono<PersonalScheduleDocument>
}
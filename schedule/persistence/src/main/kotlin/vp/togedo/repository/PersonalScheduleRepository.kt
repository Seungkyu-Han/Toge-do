package vp.togedo.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.document.PersonalScheduleDocument

interface PersonalScheduleRepository {

    fun findByUserId(userId: ObjectId): Mono<PersonalScheduleDocument>
}
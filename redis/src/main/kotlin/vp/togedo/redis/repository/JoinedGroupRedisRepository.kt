package vp.togedo.redis.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.model.documents.joinedGroup.JoinedGroupDocument

interface JoinedGroupRedisRepository {

    fun findById(id: ObjectId): Mono<JoinedGroupDocument>

    fun save(joinedGroupDocument: JoinedGroupDocument): Mono<Void>
}
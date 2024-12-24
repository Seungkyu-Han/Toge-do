package vp.togedo.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.document.JoinedGroupDocument

interface JoinedGroupRepository {

    fun findById(id: ObjectId): Mono<JoinedGroupDocument>

    fun save(joinedGroupDocument: JoinedGroupDocument): Mono<JoinedGroupDocument>
}
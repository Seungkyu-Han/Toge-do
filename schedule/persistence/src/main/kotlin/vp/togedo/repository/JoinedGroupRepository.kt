package vp.togedo.repository

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.model.documents.joinedGroup.JoinedGroupDocument

interface JoinedGroupRepository {

    fun findById(id: ObjectId): Mono<JoinedGroupDocument>

    fun save(joinedGroupDocument: JoinedGroupDocument): Mono<JoinedGroupDocument>
}
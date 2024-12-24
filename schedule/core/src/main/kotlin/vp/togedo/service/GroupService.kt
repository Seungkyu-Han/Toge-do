package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.document.GroupDocument

interface GroupService {

    fun createGroup(name: String, members: List<ObjectId>): Mono<GroupDocument>


}
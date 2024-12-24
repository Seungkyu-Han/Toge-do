package vp.togedo.document

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono

@Document(collection = "joined_group")
data class JoinedGroupDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("groups")
    val groups: MutableList<ObjectId> = mutableListOf()
){
    fun addGroup(id: ObjectId): Mono<JoinedGroupDocument> {
        return Mono.fromCallable {
            this.groups.add(id)
            this
        }
    }

    fun removeGroup(id: ObjectId): Mono<JoinedGroupDocument> {
        return Mono.fromCallable {
            this.groups.remove(id)
            this
        }
    }
}

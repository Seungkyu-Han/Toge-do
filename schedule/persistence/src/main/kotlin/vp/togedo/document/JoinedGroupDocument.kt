package vp.togedo.document

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import vp.togedo.util.exception.group.AlreadyJoinedGroupException
import vp.togedo.util.exception.group.NotJoinedGroupException

@Document(collection = "joined_group")
data class JoinedGroupDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("groups")
    val groups: MutableSet<ObjectId> = mutableSetOf()
){
    fun addGroup(id: ObjectId): Mono<JoinedGroupDocument> {
        return Mono.fromCallable {

            if(this.groups.contains(id))
                throw AlreadyJoinedGroupException("이미 포함된 그룹입니다.")

            this.groups.add(id)
            this
        }
    }

    fun removeGroup(id: ObjectId): Mono<JoinedGroupDocument> {
        return Mono.fromCallable {

            if(!this.groups.remove(id))
                throw NotJoinedGroupException("포함되지 않은 그룹입니다.")
            this
        }
    }
}

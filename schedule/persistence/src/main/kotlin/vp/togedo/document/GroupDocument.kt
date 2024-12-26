package vp.togedo.document

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import vp.togedo.util.exception.group.AlreadyJoinedGroupException
import vp.togedo.util.exception.group.NotJoinedGroupException

@Document(collection = "groups")
data class GroupDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId = ObjectId.get(),

    @JsonProperty("name")
    var name: String,

    @JsonProperty("members")
    val members: MutableSet<ObjectId> = mutableSetOf()
){
    fun changeName(name: String): Mono<GroupDocument> {
        return Mono.fromCallable {
            this.name = name
            this
        }
    }

    fun addMember(id: ObjectId): Mono<GroupDocument>{
        return Mono.fromCallable {
            if (this.members.contains(id))
                throw AlreadyJoinedGroupException("이미 포함된 그룹입니다.")
            this.members.add(id)
            this
        }
    }

    fun removeMember(id: ObjectId): Mono<GroupDocument>{
        return Mono.fromCallable {
            if(!this.members.remove(id))
                throw NotJoinedGroupException("포함되지 않은 그룹입니다.")
            this
        }
    }
}

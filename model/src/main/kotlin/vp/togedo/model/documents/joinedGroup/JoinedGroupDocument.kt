package vp.togedo.model.documents.joinedGroup

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "joined_groups")
data class JoinedGroupDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("groups")
    val groups: MutableList<ObjectId> = mutableListOf()
){

}
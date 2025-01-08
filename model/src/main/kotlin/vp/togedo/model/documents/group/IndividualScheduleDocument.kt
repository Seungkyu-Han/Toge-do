package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("individual_schedules")
data class IndividualScheduleDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("individualScheduleMap")
    val individualScheduleMap: MutableMap<ObjectId, IndividualScheduleList>
)

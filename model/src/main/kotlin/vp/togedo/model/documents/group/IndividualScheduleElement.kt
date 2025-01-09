package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId

data class IndividualScheduleElement(
    @JsonProperty("id")
    val id: ObjectId = ObjectId.get(),
    @JsonProperty("startTime")
    val startTime: String,
    @JsonProperty("endTime")
    val endTime: String
)

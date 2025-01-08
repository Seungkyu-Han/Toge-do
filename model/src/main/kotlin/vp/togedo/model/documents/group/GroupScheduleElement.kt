package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId

data class GroupScheduleElement(
    @JsonProperty("id")
    val id: ObjectId = ObjectId.get(),

    @JsonProperty("name")
    var name: String,

    @JsonProperty("startDate")
    var startDate: Long,

    @JsonProperty("endDate")
    var endDate: Long,

    @JsonProperty("startTime")
    var startTime: String,

    @JsonProperty("endTime")
    var endTime: String,

    @JsonProperty("state")
    var state: GroupScheduleStateEnum = GroupScheduleStateEnum.DISCUSSING,

    @JsonProperty("confirmedUser")
    var confirmedUser: MutableSet<ObjectId> = mutableSetOf(),

    @JsonProperty("confirmedStartDate")
    var confirmedStartDate: String? = null,

    @JsonProperty("confirmedEndDate")
    var confirmedEndDate: String? = null
)

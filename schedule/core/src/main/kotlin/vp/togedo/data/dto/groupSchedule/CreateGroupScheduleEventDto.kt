package vp.togedo.data.dto.groupSchedule

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateGroupScheduleEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("name")
    val name: String
)
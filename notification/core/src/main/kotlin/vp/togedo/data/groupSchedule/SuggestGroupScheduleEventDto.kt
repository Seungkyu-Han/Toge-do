package vp.togedo.data.groupSchedule

import com.fasterxml.jackson.annotation.JsonProperty

data class SuggestGroupScheduleEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("name")
    val name: String
)
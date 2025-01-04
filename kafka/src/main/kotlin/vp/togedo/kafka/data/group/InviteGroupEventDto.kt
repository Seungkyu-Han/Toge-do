package vp.togedo.kafka.data.group

import com.fasterxml.jackson.annotation.JsonProperty

data class InviteGroupEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("name")
    val name: String
)
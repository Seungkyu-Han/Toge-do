package vp.togedo.data.dto.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class FriendRequestEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("sender")
    val sender: String,
)
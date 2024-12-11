package vp.togedo.data.notification

import com.fasterxml.jackson.annotation.JsonProperty

data class FriendRequestEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("sender")
    val sender: String,
)

data class FriendApproveEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("sender")
    val sender: String
)
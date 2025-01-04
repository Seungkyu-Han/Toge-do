package vp.togedo.kafka.data.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class FriendRequestEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("sender")
    val sender: String,
    @JsonProperty("image")
    val image: String?
)

data class FriendApproveEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("sender")
    val sender: String,
    @JsonProperty("image")
    val image: String?
)
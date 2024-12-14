package vp.togedo.data.notification

import com.fasterxml.jackson.annotation.JsonProperty

data class FriendRequestEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("deviceToken")
    val deviceToken: String?,
    @JsonProperty("sender")
    val sender: String,
    @JsonProperty("image")
    val image: String?
)

data class FriendApproveEventDto(
    @JsonProperty("receiverId")
    val receiverId: String,
    @JsonProperty("deviceToken")
    val deviceToken: String?,
    @JsonProperty("sender")
    val sender: String,
    @JsonProperty("image")
    val image: String?
)
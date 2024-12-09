package vp.togedo.data.dto.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class FriendRequestEventDto(
    @JsonProperty("friendId")
    val friendId: String
)

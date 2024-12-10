package vp.togedo.data.dto.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class FriendApproveEventDto(
    @JsonProperty("friendId")
    val friendId: String
)
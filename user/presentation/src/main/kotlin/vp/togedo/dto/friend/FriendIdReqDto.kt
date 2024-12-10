package vp.togedo.dto.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class FriendIdReqDto(
    @JsonProperty("friendId")
    val friendId: String
)

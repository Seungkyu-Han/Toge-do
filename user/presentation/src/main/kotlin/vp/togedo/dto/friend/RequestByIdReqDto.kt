package vp.togedo.dto.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestByIdReqDto(
    @JsonProperty("friendId")
    val friendId: String
)

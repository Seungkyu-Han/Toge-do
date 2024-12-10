package vp.togedo.dto.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class ApproveReqDto(
    @JsonProperty("friendId")
    val friendId: String
)

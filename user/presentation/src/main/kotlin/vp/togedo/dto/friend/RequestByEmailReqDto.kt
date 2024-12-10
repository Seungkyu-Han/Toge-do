package vp.togedo.dto.friend

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestByEmailReqDto(
    @JsonProperty("email")
    val email: String
)

package vp.togedo.data.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageReqDto(
    @JsonProperty("message")
    val message: String
)

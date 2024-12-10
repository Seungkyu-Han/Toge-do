package vp.togedo.dto.user

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckValidResDto(
    @JsonProperty("result")
    val result: Boolean
)

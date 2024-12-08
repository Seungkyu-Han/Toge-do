package vp.togedo.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CheckValidResDto(
    @JsonProperty("result")
    val result: Boolean
)

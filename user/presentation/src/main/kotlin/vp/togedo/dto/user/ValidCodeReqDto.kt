package vp.togedo.dto.user

import com.fasterxml.jackson.annotation.JsonProperty

data class ValidCodeReqDto(
    @JsonProperty("email")
    val email: String
)

package vp.togedo.data.dto.group

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateGroupReqDto(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
)

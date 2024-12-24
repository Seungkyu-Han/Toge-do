package vp.togedo.data.dto.group

import com.fasterxml.jackson.annotation.JsonProperty

data class GroupDto(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("members")
    val members: List<String>
)

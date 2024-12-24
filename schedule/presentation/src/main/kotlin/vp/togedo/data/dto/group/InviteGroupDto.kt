package vp.togedo.data.dto.group

import com.fasterxml.jackson.annotation.JsonProperty

data class InviteGroupDto(
    @JsonProperty("groupId")
    val groupId: String,
    @JsonProperty("userId")
    val userId: String
)

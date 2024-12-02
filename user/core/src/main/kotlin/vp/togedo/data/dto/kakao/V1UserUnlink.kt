package vp.togedo.data.dto.kakao

import com.fasterxml.jackson.annotation.JsonProperty

data class V1UserUnlink(
    @JsonProperty("id")
    val id: Long,
)

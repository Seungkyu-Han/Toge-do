package vp.togedo.data.dto.kakao

import com.fasterxml.jackson.annotation.JsonProperty

data class OauthLogout(
    @JsonProperty("id")
    val id: Long
)

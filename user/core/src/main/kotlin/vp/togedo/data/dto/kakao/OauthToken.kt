package vp.togedo.data.dto.kakao

import com.fasterxml.jackson.annotation.JsonProperty

data class OauthTokenRes(
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Int,
    @JsonProperty("refresh_token")
    val refreshToken: String,
    @JsonProperty("refresh_token_expires_in")
    private val refreshTokenExpiresIn: Int,
    @JsonProperty("scope")
    val scope: String,
)
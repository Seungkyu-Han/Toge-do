package vp.togedo.data.dto.google

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleAccessToken(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("expires_in")
    val expiresIn: Int,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("scope")
    val scope: String,
    @JsonProperty("id_token")
    val idToken: String,
)
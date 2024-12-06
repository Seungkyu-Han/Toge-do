package vp.togedo.data.dto.google

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleUserInfo(
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("email")
    val email: String?,
    @JsonProperty("verified_email")
    val verifiedEmail: Boolean?,
    @JsonProperty("name")
    val name: String?,
    @JsonProperty("given_name")
    val givenName: String?,
    @JsonProperty("family_name")
    val familyName: String?,
    @JsonProperty("picture")
    val picture: String?,
    @JsonProperty("locale")
    val locale: String?
)
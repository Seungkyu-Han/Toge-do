package vp.togedo.dto.user

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.codec.multipart.FilePart

data class UserInfoReqDto(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("email")
    val email: String,
    @JsonProperty("image")
    val image: FilePart?,
    @JsonProperty("isProfileImageDelete")
    val isProfileImageDelete: Boolean
)
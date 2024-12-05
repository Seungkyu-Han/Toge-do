package vp.togedo.dto

import org.springframework.http.codec.multipart.FilePart

data class UserInfoReqDto(
    val name: String,
    val email: String,
    val image: FilePart?
)
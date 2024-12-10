package vp.togedo.dto.user

data class LoginRes(
    val accessToken: String,
    val refreshToken: String,
)

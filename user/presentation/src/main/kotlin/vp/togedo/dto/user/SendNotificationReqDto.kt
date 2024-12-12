package vp.togedo.dto.user

data class SendNotificationReqDto(
    val isAgree: Boolean,
    val deviceToken: String
)
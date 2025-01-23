package vp.togedo.data.enums

enum class WebSocketEventEnum(
    val value: Int,
    val message: String) {

    AUTHORIZATION_ERROR(1, "인증과정에서 에러가 발생했습니다.")
}
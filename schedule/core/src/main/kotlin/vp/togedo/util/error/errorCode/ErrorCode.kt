package vp.togedo.util.error.errorCode

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다.")
}
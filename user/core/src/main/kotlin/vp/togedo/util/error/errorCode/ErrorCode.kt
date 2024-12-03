package vp.togedo.util.error.errorCode

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {

    USER_NOT_FOUND_BY_OAUTH(HttpStatus.NOT_FOUND, "쇼셜 로그인으로 해당 계정을 찾을 수 없습니다."),
}
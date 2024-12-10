package vp.togedo.util.error.errorCode

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {

    USER_NOT_FOUND_BY_OAUTH(HttpStatus.NOT_FOUND, "쇼셜 로그인으로 해당 계정을 찾을 수 없습니다."),
    LOGIN_UNEXPECTED_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "사용자 로그인 중 에러가 발생했습니다."),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    EMPTY_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
    INVALID_OBJECT_USERID(HttpStatus.FORBIDDEN, "token에서 유저 정보를 획득할 수 없습니다."),
    ALREADY_FRIEND(HttpStatus.CONFLICT, "이미 친구인 사용자입니다.")
}
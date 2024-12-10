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

    ALREADY_FRIEND(HttpStatus.CONFLICT, "이미 친구인 사용자입니다."),
    ALREADY_FRIEND_REQUESTED(HttpStatus.CONFLICT, "이미 친구 요청이 전송된 상태입니다."),
    NO_REQUESTED(HttpStatus.NOT_FOUND, "해당 사용자에게 친구 요청이 오지 않았습니다"),
    NOT_FRIEND(HttpStatus.BAD_REQUEST, "친구인 유저가 아닙니다.")
}
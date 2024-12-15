package vp.togedo.util.error.errorCode

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다."),

    SCHEDULE_INFO_CANT_FIND(HttpStatus.NOT_FOUND, "해당 유저의 스케줄 정보가 존재하지 않습니다.")
}
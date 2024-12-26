package vp.togedo.util.error.errorCode

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다."),

    SCHEDULE_INFO_CANT_FIND(HttpStatus.NOT_FOUND, "해당 유저의 스케줄 정보가 존재하지 않습니다."),
    SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "스케줄의 시간이 충돌합니다."),
    END_TIME_BEFORE_START_TIME(HttpStatus.BAD_REQUEST, "스케줄의 종료시간이 시작시간보다 빠릅니다."),
    BAD_SCHEDULE_TIME(HttpStatus.BAD_REQUEST, "스케줄의 종료시간이 시작시간보다 빠릅니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 스케줄이 존재하지 않습니다"),

    ALREADY_JOINED_GROUP(HttpStatus.CONFLICT, "이미 포함된 그룹입니다."),
    REQUIRE_MORE_MEMBER(HttpStatus.BAD_REQUEST, "멤버가 더 필요합니다."),
    NOT_JOINED_GROUP(HttpStatus.BAD_REQUEST, "포함되지 않은 그룹입니다."),
    NOT_EXIST_GROUP(HttpStatus.NOT_FOUND, "존재하지 않는 그룹입니다."),

}
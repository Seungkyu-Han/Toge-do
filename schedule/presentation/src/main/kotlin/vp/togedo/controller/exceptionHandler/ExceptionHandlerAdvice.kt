package vp.togedo.controller.exceptionHandler

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import vp.togedo.util.error.exception.GroupException
import vp.togedo.util.error.exception.GroupScheduleException
import vp.togedo.util.error.exception.ScheduleException
import vp.togedo.util.error.exception.UserException
import java.util.regex.PatternSyntaxException

@RestControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(UserException::class)
    fun userExceptionHandler(userException: UserException): ResponseEntity<String> {
        return ResponseEntity
            .status(userException.errorCode.status)
            .body(userException.errorCode.message)
    }

    @ExceptionHandler(ScheduleException::class)
    fun scheduleExceptionHandler(scheduleException: ScheduleException): ResponseEntity<String> {
        return ResponseEntity
            .status(scheduleException.errorCode.status)
            .body(scheduleException.errorCode.message)
    }

    @ExceptionHandler(GroupException::class)
    fun groupExceptionHandler(groupException: GroupException): ResponseEntity<String> {
        return ResponseEntity
            .status(groupException.errorCode.status)
            .body(groupException.errorCode.message)
    }

    @ExceptionHandler(GroupScheduleException::class)
    fun groupExceptionHandler(groupScheduleException: GroupScheduleException): ResponseEntity<String> {
        return ResponseEntity
            .status(groupScheduleException.errorCode.status)
            .body(groupScheduleException.errorCode.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentExceptionHandler(illegalArgumentException: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity
            .status(400)
            .body("부정확한 데이터가 입력되었습니다.")
    }

    @ExceptionHandler(PatternSyntaxException::class)
    fun patternSyntaxExceptionHandler(patternSyntaxException: PatternSyntaxException): ResponseEntity<String> {
        return ResponseEntity
            .status(400)
            .body("부정확한 데이터가 입력되었습니다.")
    }
}
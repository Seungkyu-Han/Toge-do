package vp.togedo.controller.exceptionHandler

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import vp.togedo.util.error.exception.UserException

@RestControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler(UserException::class)
    fun userExceptionHandler(userException: UserException): ResponseEntity<String> {
        return ResponseEntity
            .status(userException.errorCode.status)
            .body(userException.errorCode.message)
    }

}
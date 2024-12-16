package vp.togedo.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fallback")
class CircuitBreakerController {

    @GetMapping("/user")
    fun defaultFallback(): ResponseEntity<String>{
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            "현재 유저 서버가 준비 중입니다. 죄송합니다."
        )
    }

    @GetMapping("/schedule")
    fun scheduleFallback(): ResponseEntity<String>{
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            "현재 스케줄 서버가 준비 중입니다. 죄송합니다."
        )
    }
}
package vp.togedo.util

import org.springframework.stereotype.Component

@Component
class ValidationUtil {

    fun verificationCode(): String = (100000 .. 999999).random().toString()
}
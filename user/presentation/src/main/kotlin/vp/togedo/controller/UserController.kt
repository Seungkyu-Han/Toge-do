package vp.togedo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import vp.togedo.connector.UserConnector

@RestController
class UserController(
    private val userConnector: UserConnector
) {

    @GetMapping("/api/v1/user/kakao-login")
    fun kakaoLogin(@RequestParam code: String) = userConnector.login(code)
}
package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import vp.togedo.connector.UserConnector
import vp.togedo.dto.LoginRes

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "유저 API", description = "회원가입 및 로그인과 관련된 API입니다.")
class UserController(
    private val userConnector: UserConnector
) {

    @GetMapping("/kakao-login")
    @Operation(summary = "카카오 oauth를 사용한 로그인")
    @Parameters(
        Parameter(name = "code", description = "카카오 Oauth에서 발급받은 코드")
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "회원가입 혹은 로그인 성공",
            content = [Content(schema = Schema(implementation = LoginRes::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "503", description = "로그인 중 알 수 없는 에러 발생",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])

    )
    suspend fun kakaoLogin(@RequestParam code: String): ResponseEntity<LoginRes>{
        return userConnector.kakaoLogin(code)
            .map { ResponseEntity.ok().body(it) }.awaitSingle()
    }

    @GetMapping("/reissue")
    @Operation(summary = "refresh token을 사용하여 access token을 재발급")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
            content = [Content(schema = Schema(implementation = LoginRes::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "유효하지 않은 토큰",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])

    )
    fun reissue(@RequestHeader("Authorization") refreshToken: String): ResponseEntity<LoginRes> {
        return ResponseEntity.ok().body(userConnector.reissueAccessToken(refreshToken))
    }
}
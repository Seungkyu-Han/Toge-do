package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import vp.togedo.connector.EmailConnector
import vp.togedo.connector.UserConnector
import vp.togedo.dto.user.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "유저 API", description = "회원가입 및 로그인과 관련된 API입니다.")
class UserController(
    private val userConnector: UserConnector,
    private val emailConnector: EmailConnector,
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

    @GetMapping("/google-login")
    @Operation(summary = "구글 oauth를 사용한 로그인")
    @Parameters(
        Parameter(name = "code", description = "구글 Oauth에서 발급받은 코드")
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "회원가입 혹은 로그인 성공",
            content = [Content(schema = Schema(implementation = LoginRes::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "503", description = "로그인 중 알 수 없는 에러 발생",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun googleLogin(@RequestParam code: String): ResponseEntity<LoginRes>{
        return userConnector.googleLogin(withContext(Dispatchers.IO) {
            URLDecoder.decode(code, StandardCharsets.UTF_8.toString())
        }).map { ResponseEntity.ok().body(it) }.awaitSingle()
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
    fun reissue(@Parameter(hidden = true) @RequestHeader("Authorization") refreshToken: String): ResponseEntity<LoginRes> {
        return ResponseEntity.ok().body(userConnector.reissueAccessToken(refreshToken))
    }

    @PutMapping("/info", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "사용자 정보 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "사용자 정보 변경 성공",
            content = [Content(schema = Schema(implementation = UserInfoResDto::class))]))
    suspend fun updateInfo(
        @ModelAttribute userInfoReqDto: UserInfoReqDto,
        @Parameter(hidden = true, required = false) @RequestHeader("Authorization") accessToken: String?): ResponseEntity<UserInfoResDto> {
        return ResponseEntity.ok()
            .body(userConnector.updateUserInfo(userInfoReqDto, userConnector.extractUserIdByToken(accessToken)))
    }

    @GetMapping("/info")
    @Operation(summary = "사용자 정보 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
            content = [Content(schema = Schema(implementation = UserInfoResDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "사용자 정보 조회 실패",
            content = [Content(schema = Schema(implementation = String::class),
                mediaType = MediaType.TEXT_PLAIN_VALUE)]))
    suspend fun findInfo(
        @Parameter(hidden = true, required = false) @RequestHeader("Authorization") accessToken: String?): ResponseEntity<UserInfoResDto> {
        return ResponseEntity.ok()
            .body(userConnector.findUserInfo(userConnector.extractUserIdByToken(accessToken)))
    }

    @PostMapping("/valid-code")
    @Operation(summary = "유효성 검사를 위해 이메일에 인증코드를 전송")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "이메일 전송 성공",
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)])
    )
    fun requestValidCode(
        @RequestBody validCodeReqDto: ValidCodeReqDto
    ): Mono<ResponseEntity<HttpStatus>>{
        return emailConnector.requestValidCode(validCodeReqDto.email)
            .thenReturn(ResponseEntity.ok().build())
    }

    @GetMapping("/check-valid")
    @Operation(summary = "인증번호를 사용하여 이메일 검사")
    @Parameters(
        Parameter(name = "code", description = "인증번호"),
        Parameter(name = "email", description = "유효성 검사 할 이메일"),
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "유효성 검사 성공",
            content = [Content(schema = Schema(implementation = CheckValidResDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)])
    )
    fun checkValidCode(
        @RequestParam code: String,
        @RequestParam email: String): Mono<ResponseEntity<CheckValidResDto>> =
        emailConnector.checkValidCode(code = code, email = email)
            .map {
                ResponseEntity.ok().body(CheckValidResDto(it))
            }


    @PatchMapping("/send-notification")
    @Operation(summary = "앱 푸쉬 알림 동의 여부")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "변경 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음")
    )
    fun changeNotification(
        @RequestBody sendNotificationReqDto:SendNotificationReqDto,
        @Parameter(hidden = true, required = false) @RequestHeader("Authorization") accessToken: String?
    ): Mono<ResponseEntity<HttpStatus>> {
        return userConnector.changeNotification(
            isAgree = sendNotificationReqDto.isAgree,
            deviceToken = sendNotificationReqDto.deviceToken,
            id = userConnector.extractUserIdByToken(accessToken)
        ).map{
            ResponseEntity.ok().build()
        }
    }
}
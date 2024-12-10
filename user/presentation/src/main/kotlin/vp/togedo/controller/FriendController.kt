package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.config.IdConfig
import vp.togedo.connector.FriendConnector
import vp.togedo.dto.friend.FriendInfoResDto
import vp.togedo.dto.friend.RequestByEmailReqDto
import vp.togedo.dto.friend.RequestByIdReqDto

@RestController
@RequestMapping("/api/v1/friend")
@Tag(name = "친구 API", description = "친구 신청 및 조회와 관련된 API입니다.")
class FriendController(
    private val idConfig: IdConfig,
    private val friendConnector: FriendConnector
) {
    @GetMapping("/list")
    @Operation(summary = "친구 목록을 조회(정렬 X)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "친구 목록 조회 성공",
            content = [Content(schema = Schema(implementation = FriendInfoResDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun getFriendsInfo(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String
    ): ResponseEntity<Flux<FriendInfoResDto>> {
        return ResponseEntity.ok().body(
            friendConnector.getFriendsInfo(idConfig.objectIdProvider(userId))
                .map{
                    FriendInfoResDto(
                        id = it.id!!.toString(),
                        name = it.name,
                        image = it.profileImageUrl,
                        email = it.email,
                    )
                }
        )
    }

    @PostMapping("/request")
    @Operation(summary = "id를 사용하여 친구요청")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "친구 요청 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
        ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음"),
        ApiResponse(responseCode = "409", description = "이미 친구인 사용자임"),
    )
    fun requestFriendById(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody requestByIdReqDto: RequestByIdReqDto): Mono<ResponseEntity<HttpStatus>> {
        return friendConnector.requestFriendById(
            id = idConfig.objectIdProvider(userId),
            friendId = ObjectId(requestByIdReqDto.friendId))
            .map {
                ResponseEntity.ok().build()
            }
    }

    @PostMapping("/request-email")
    @Operation(summary = "email을 사용하여 친구요청")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "친구 요청 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
        ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음"),
        ApiResponse(responseCode = "409", description = "이미 친구인 사용자임"),
    )
    fun requestFriendByEmail(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody requestByEmailReqDto: RequestByEmailReqDto): Mono<ResponseEntity<HttpStatus>> {
        return friendConnector.requestFriendByEmail(
            id = idConfig.objectIdProvider(userId),
            email = requestByEmailReqDto.email)
            .map {
                ResponseEntity.ok().build()
            }
    }


}
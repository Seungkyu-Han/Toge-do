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
import vp.togedo.dto.friend.*

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
    suspend fun requestFriendById(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody friendIdReqDto: FriendIdReqDto): ResponseEntity<HttpStatus> {
        friendConnector.requestFriendById(
            id = idConfig.objectIdProvider(userId),
            friendId = ObjectId(friendIdReqDto.friendId))
        return ResponseEntity.ok().build()
    }

    @PostMapping("/request-email")
    @Operation(summary = "email을 사용하여 친구요청")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "친구 요청 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
        ApiResponse(responseCode = "404", description = "해당 유저가 존재하지 않음"),
        ApiResponse(responseCode = "409", description = "이미 친구인 사용자임"),
    )
    suspend fun requestFriendByEmail(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody requestByEmailReqDto: RequestByEmailReqDto): ResponseEntity<HttpStatus> {
        friendConnector.requestFriendByEmail(
            id = idConfig.objectIdProvider(userId),
            email = requestByEmailReqDto.email)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/request-list")
    @Operation(summary = "요청 친구 목록을 조회(정렬 X)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "요청 친구 목록 조회 성공",
            content = [Content(schema = Schema(implementation = RequestFriendResDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun getRequestFriends(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String
    ): ResponseEntity<Flux<RequestFriendResDto>> {
        return ResponseEntity.ok().body(
            friendConnector.getFriendRequests(idConfig.objectIdProvider(userId))
                .map{
                    RequestFriendResDto(
                        name = it.name,
                        image = it.profileImageUrl,
                        id = it.id.toString(),
                    )
                }
        )
    }

    @PostMapping("/approve")
    @Operation(summary = "해당 사용자와의 친구를 수락")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "403", description = "권한 없음"),
        ApiResponse(responseCode = "404", description = "친구 요청이 온 적 없음"),
        ApiResponse(responseCode = "409", description = "이미 친구인 사용자")
    )
    suspend fun approveFriend(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody friendIdReqDto: FriendIdReqDto
    ): ResponseEntity<HttpStatus>{
        friendConnector.approveFriend(
            id = idConfig.objectIdProvider(userId),
            friendId = ObjectId(friendIdReqDto.friendId)
        )
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/disconnect")
    @Operation(summary = "친구 관계를 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공"),
        ApiResponse(responseCode = "400", description = "친구가 아닌 사용자"),
        ApiResponse(responseCode = "403", description = "권한 없음")
    )
    fun disconnectFriend(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody friendIdReqDto: FriendIdReqDto): Mono<ResponseEntity<HttpStatus>>{
        return friendConnector.disconnectFriend(
            id = idConfig.objectIdProvider(userId),
            friendId = ObjectId(friendIdReqDto.friendId)
        ).map{
            ResponseEntity.ok().build()
        }
    }

    @DeleteMapping("/request-reject")
    @Operation(summary = "친구 요청을 거부")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "친구 요청 거부 성공"),
        ApiResponse(responseCode = "403", description = "권한이 없음"),
        ApiResponse(responseCode = "404", description = "해당 사용자로부터 요청이 없음")
    )
    fun rejectFriend(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestParam senderId: String): Mono<ResponseEntity<HttpStatus>>{
        return friendConnector.rejectFriend(
            receiverId = idConfig.objectIdProvider(userId),
            senderId = ObjectId(senderId)
        ).map{
            ResponseEntity.ok().build()
        }
    }

}
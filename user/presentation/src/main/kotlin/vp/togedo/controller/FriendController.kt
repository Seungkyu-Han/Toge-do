package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import vp.togedo.config.IdConfig
import vp.togedo.connector.FriendConnector
import vp.togedo.dto.FriendInfoResDto

@RestController
@RequestMapping("/api/v1/friend")
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
}
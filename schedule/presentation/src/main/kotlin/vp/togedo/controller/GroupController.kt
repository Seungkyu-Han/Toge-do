package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.config.IdComponent
import vp.togedo.connector.GroupConnector
import vp.togedo.data.dto.group.CreateGroupReqDto
import vp.togedo.data.dto.group.GroupDto
import vp.togedo.data.dto.group.InviteGroupDto
import vp.togedo.data.dto.group.UpdateGroupReqDto

@RestController
@RequestMapping("/api/v1/group")
@Tag(name = "그룹 API", description = "그룹을 관리하는 API입니다.")
class GroupController(
    private val groupConnector: GroupConnector,
    private val idComponent: IdComponent
) {

    @GetMapping("/groups")
    @Operation(summary = "본인이 속한 그룹을 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK",
            content = [Content(schema = Schema(implementation = GroupDto::class))]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun getGroups(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String
    ): ResponseEntity<Flux<GroupDto>>{
        return ResponseEntity.ok().body(groupConnector.readGroups(
            userId = idComponent.objectIdProvider(userId)
        ))
    }

    @PostMapping("/create")
    @Operation(summary = "그룹을 생성")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "OK",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun createGroup(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody createGroupReqDto: CreateGroupReqDto
    ): Mono<ResponseEntity<HttpStatus>> {
        return groupConnector.createGroup(
            userId = idComponent.objectIdProvider(userId),
            createGroupReqDto = createGroupReqDto
        ).then(Mono.just(ResponseEntity.status(201).build()))
    }

    @PatchMapping("/invite")
    @Operation(summary = "그룹에 사용자를 초대")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun inviteGroup(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody inviteGroupDto: InviteGroupDto
    ): Mono<ResponseEntity<HttpStatus>> {
        idComponent.objectIdProvider(userId)
        return groupConnector.addUserToGroup(
            addedId = inviteGroupDto.userId,
            groupId = inviteGroupDto.groupId
            ).then(Mono.just(ResponseEntity.ok().build()))
    }

    @PutMapping("/update")
    @Operation(summary = "그룹을 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun updateGroup(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody updateGroupReqDto: UpdateGroupReqDto
    ): Mono<ResponseEntity<HttpStatus>> {
        idComponent.objectIdProvider(userId)
        return groupConnector.modifyGroup(
            updateGroupReqDto = updateGroupReqDto
        ).then(Mono.just(ResponseEntity.ok().build()))
    }

    @PatchMapping("/exit")
    @Operation(summary = "해당 그룹에서 탈퇴")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "OK",
            content = [Content(schema = Schema(implementation = HttpStatus::class))]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun exitGroup(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestParam groupId: String
    ):Mono<ResponseEntity<HttpStatus>> = groupConnector.exitGroup(
        userId = idComponent.objectIdProvider(userId),
        groupId = groupId,
    ).then(Mono.just(ResponseEntity.ok().build()))
}
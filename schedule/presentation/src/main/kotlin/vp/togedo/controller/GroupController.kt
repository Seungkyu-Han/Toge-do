package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import vp.togedo.config.IdComponent
import vp.togedo.connector.GroupConnector
import vp.togedo.data.dto.group.GroupDto

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


}
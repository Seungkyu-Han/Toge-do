package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.bson.types.ObjectId
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.config.IdComponent
import vp.togedo.connector.GroupScheduleConnector
import vp.togedo.data.dto.groupSchedule.CreateGroupScheduleReqDto
import vp.togedo.data.dto.groupSchedule.GroupScheduleDetailDto
import vp.togedo.data.dto.groupSchedule.GroupScheduleDto

@RestController
@RequestMapping("/api/v1/group-schedule")
@Tag(name = "공유 스케줄 API", description = "공유 스케줄을 관리하는 API입니다.")
class GroupScheduleController(
    private val groupScheduleConnector: GroupScheduleConnector,
    private val idComponent: IdComponent
) {
    @PostMapping("/create")
    @Operation(summary = "그룹에 공유 일정을 생성")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "생성 성공",
            content = [Content(schema = Schema(implementation = GroupScheduleDetailDto::class))]),
        ApiResponse(responseCode = "403", description = "권한 없음",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun createGroupSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody createGroupScheduleReqDto: CreateGroupScheduleReqDto
    ): Mono<ResponseEntity<GroupScheduleDetailDto>> {
        return groupScheduleConnector.createGroupSchedule(
            userId = idComponent.objectIdProvider(userId),
            groupId = ObjectId(createGroupScheduleReqDto.groupId),
            name = createGroupScheduleReqDto.name,
            startDate = createGroupScheduleReqDto.startDate,
            endDate = createGroupScheduleReqDto.endDate,
        ).map{
            ResponseEntity.ok().body(it)
        }
    }

    @GetMapping("/schedules/{groupId}")
    @Operation(summary = "해당 그룹의 일정들을 조회")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "조회 성공",
            content = [Content(schema = Schema(implementation = GroupScheduleDto::class))]),
        ApiResponse(responseCode = "403", description = "권한 없음",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun readGroupSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @PathVariable groupId: String,
    ): ResponseEntity<Flux<GroupScheduleDto>> =
        ResponseEntity.ok().body(groupScheduleConnector.readGroupSchedules(groupId = ObjectId(groupId)))

}
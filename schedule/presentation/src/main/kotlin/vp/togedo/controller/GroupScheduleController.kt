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
import vp.togedo.config.IdComponent
import vp.togedo.connector.GroupScheduleConnector
import vp.togedo.data.dto.groupSchedule.CreateGroupScheduleReqDto
import vp.togedo.data.dto.groupSchedule.GroupScheduleDetailDto
import vp.togedo.data.dto.groupSchedule.GroupScheduleDto
import vp.togedo.data.dto.groupSchedule.UpdateGroupScheduleReqDto

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
        ApiResponse(responseCode = "200", description = "조회 성공",
            content = [Content(schema = Schema(implementation = GroupScheduleDto::class))]),
        ApiResponse(responseCode = "403", description = "권한 없음",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun readGroupSchedules(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @PathVariable groupId: String,
    ): ResponseEntity<Flux<GroupScheduleDto>> =
        ResponseEntity.ok().body(groupScheduleConnector.readGroupSchedules(groupId = ObjectId(groupId)))

    @GetMapping("/schedule")
    @Operation(summary = "해당 그룹에서 선택한 일정을 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "조회 성공",
            content = [Content(schema = Schema(implementation = GroupScheduleDetailDto::class))]),
        ApiResponse(responseCode = "403", description = "권한 없음",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun readGroupSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestParam groupId: String,
        @RequestParam scheduleId: String
    ): Mono<ResponseEntity<GroupScheduleDetailDto>> =
        groupScheduleConnector.readGroupSchedule(
            groupId = ObjectId(groupId),
            scheduleId = ObjectId(scheduleId)
        ).map{
            ResponseEntity.ok().body(it)
        }

    @PutMapping("/update")
    @Operation(summary = "공유 일정을 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "수정 성공",
            content = [Content(schema = Schema(implementation = GroupScheduleDetailDto::class))]),
        ApiResponse(responseCode = "403", description = "권한 없음",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun updateGroupSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody updateGroupScheduleReqDto: UpdateGroupScheduleReqDto
    ):  Mono<ResponseEntity<GroupScheduleDetailDto>> =
        groupScheduleConnector.updateGroupSchedule(
            updateGroupScheduleReqDto = updateGroupScheduleReqDto
        ).map{
            ResponseEntity.ok().body(it)
        }

    @DeleteMapping("/delete")
    @Operation(summary = "공유 일정을 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "삭제 성공"),
        ApiResponse(responseCode = "403", description = "권한 없음",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]),
        ApiResponse(responseCode = "404", description = "해당 공유 일정을 찾을 수 없음",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    fun deleteGroupSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestParam groupId: String,
        @RequestParam scheduleId: String
    ): Mono<ResponseEntity<HttpStatus>> =
        groupScheduleConnector.deleteGroupSchedule(
            groupId = ObjectId(groupId),
            scheduleId = ObjectId(scheduleId)
        ).then(Mono.fromCallable { ResponseEntity.ok().build() })

}
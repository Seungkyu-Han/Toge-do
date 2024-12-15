package vp.togedo.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import vp.togedo.config.IdComponent
import vp.togedo.connector.FixedPersonalScheduleConnector
import vp.togedo.data.dto.fixedPersonalSchedule.CreateFixedReqDto
import vp.togedo.data.dto.fixedPersonalSchedule.FixedPersonalScheduleDto
import vp.togedo.data.dto.fixedPersonalSchedule.ReadFixedResDto
import vp.togedo.data.dto.fixedPersonalSchedule.UpdateFixedReqDto

@RestController
@RequestMapping("/api/v1/fixed-personal-schedule")
@Tag(name = "개인 고정 스케줄 API", description = "개인의 고정 스케줄을 관리하는 API입니다.")
class FixedPersonalScheduleController(
    private val fixedPersonalScheduleConnector: FixedPersonalScheduleConnector,
    private val idComponent: IdComponent
) {

    @GetMapping("/schedules")
    @Operation(summary = "개인 고정 스케줄 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "스케줄 조회 성공",
            content = [Content(schema = Schema(implementation = ReadFixedResDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun getSchedules(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String
    ): ResponseEntity<ReadFixedResDto> {
        val readFixedResDto = fixedPersonalScheduleConnector.readFixedSchedule(
            id = idComponent.objectIdProvider(userId)
        )
        return ResponseEntity.ok(readFixedResDto)
    }

    @PostMapping("/create")
    @Operation(summary = "개인 고정 스케줄 생성")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "스케줄 생성 성공",
            content = [Content(schema = Schema(implementation = ReadFixedResDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun createSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody createFixedReqDto: CreateFixedReqDto
    ): ResponseEntity<FixedPersonalScheduleDto>{
        val schedule = fixedPersonalScheduleConnector.createFixedSchedule(
            userId = idComponent.objectIdProvider(userId),
            createFixedReqDto = createFixedReqDto
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(
            FixedPersonalScheduleDto(
                id = schedule.scheduleId!!.toString(),
                startTime = schedule.startTime,
                endTime = schedule.endTime,
                title = schedule.title,
                color = schedule.color,
            )
        )
    }

    @PutMapping("/update")
    @Operation(summary = "개인 고정 스케줄 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "스케줄 수정 성공",
            content = [Content(schema = Schema(implementation = ReadFixedResDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]),
        ApiResponse(responseCode = "404", description = "해당 스케줄을 찾을 수 없습니다",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun updateSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody updateFixedReqDto: UpdateFixedReqDto
    ): ResponseEntity<FixedPersonalScheduleDto>{
        val schedule = fixedPersonalScheduleConnector.updateFixedSchedule(
            id = idComponent.objectIdProvider(userId),
            updateFixedReqDto = updateFixedReqDto
        )
        return ResponseEntity.ok().body(
            FixedPersonalScheduleDto(
                id = schedule.scheduleId!!.toString(),
                startTime = schedule.startTime,
                endTime = schedule.endTime,
                title = schedule.title,
                color = schedule.color,
            )
        )
    }

    @DeleteMapping("/delete")
    @Operation(summary = "개인 고정 스케줄 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "스케줄 삭제 성공"),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]),
        ApiResponse(responseCode = "404", description = "해당 스케줄을 찾을 수 없습니다",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun deleteSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestParam scheduleId: List<String>
    ): ResponseEntity<HttpStatus>{
        fixedPersonalScheduleConnector.deleteFixedSchedule(
            userId = idComponent.objectIdProvider(userId),
            scheduleIdList = scheduleId
        )
        return ResponseEntity.ok().build()
    }

}
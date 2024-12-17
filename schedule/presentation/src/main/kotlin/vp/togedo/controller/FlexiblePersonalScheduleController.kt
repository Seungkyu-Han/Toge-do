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
import vp.togedo.connector.FlexiblePersonalScheduleConnector
import vp.togedo.data.dao.FlexibleScheduleDao
import vp.togedo.data.dto.flexiblePersonalSchedule.CreateFlexibleReqDto
import vp.togedo.data.dto.flexiblePersonalSchedule.FlexiblePersonalScheduleDto
import vp.togedo.data.dto.flexiblePersonalSchedule.FlexiblePersonalScheduleListDto
import vp.togedo.data.dto.flexiblePersonalSchedule.UpdateFlexibleReqDto

@RestController
@RequestMapping("/api/v1/flexible-personal-schedule")
@Tag(name = "개인 가변 스케줄 API", description = "개인의 가변 스케줄을 관리하는 API입니다.")
class FlexiblePersonalScheduleController(
    private val flexiblePersonalScheduleConnector: FlexiblePersonalScheduleConnector,
    private val idComponent: IdComponent
) {

    @GetMapping("/schedules")
    @Operation(summary = "개인 고정 스케줄 조회")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "스케줄 조회 성공",
            content = [Content(schema = Schema(implementation = FlexiblePersonalScheduleListDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun getSchedules(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String
    ): ResponseEntity<FlexiblePersonalScheduleListDto>{
        return ResponseEntity.ok(
            daoToDto(
                flexiblePersonalScheduleConnector.readFlexibleSchedule(id = idComponent.objectIdProvider(userId))
            )
        )
    }

    @PostMapping("/create")
    @Operation(summary = "개인 가변 스케줄 생성")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "스케줄 생성 성공",
            content = [Content(schema = Schema(implementation = FlexiblePersonalScheduleListDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun createSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody createFlexibleReqDtoList: List<CreateFlexibleReqDto>
    ): ResponseEntity<FlexiblePersonalScheduleListDto>{
        return ResponseEntity.status(201)
            .body(
                daoToDto(flexiblePersonalScheduleConnector.createFlexibleSchedule(
                    userId = idComponent.objectIdProvider(userId),
                    createFlexibleReqDtoList = createFlexibleReqDtoList
                ))
            )
    }

    @PutMapping("/update")
    @Operation(summary = "개인 가변 스케줄 수정")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "스케줄 수정 성공",
            content = [Content(schema = Schema(implementation = FlexiblePersonalScheduleListDto::class),
                mediaType = MediaType.APPLICATION_JSON_VALUE)]),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]),
        ApiResponse(responseCode = "404", description = "해당 스케줄을 찾을 수 없습니다",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun updateSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestBody updateFlexibleReqDtoList: List<UpdateFlexibleReqDto>
    ): ResponseEntity<FlexiblePersonalScheduleListDto>{
        return ResponseEntity.ok(
            daoToDto(flexiblePersonalScheduleConnector.updateFlexibleSchedule(
                id = idComponent.objectIdProvider(userId),
                updateFlexibleReqDtoList = updateFlexibleReqDtoList
            ))
        )
    }

    @DeleteMapping("/delete")
    @Operation(summary = "개인 가변 스케줄 삭제")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "스케줄 삭제 성공"),
        ApiResponse(responseCode = "403", description = "권한 에러",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]),
        ApiResponse(responseCode = "404", description = "해당 스케줄을 찾을 수 없습니다",
            content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)])
    )
    suspend fun deleteSchedule(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @RequestParam flexibleScheduleIdList: List<String>
    ): ResponseEntity<HttpStatus>{
        flexiblePersonalScheduleConnector.deleteFlexibleSchedule(
            userId = idComponent.objectIdProvider(userId),
            flexibleScheduleIdList = flexibleScheduleIdList
        )
        return ResponseEntity.ok().build()
    }



    private fun daoToDto(flexibleScheduleDaoList: List<FlexibleScheduleDao>): FlexiblePersonalScheduleListDto {
        return FlexiblePersonalScheduleListDto(
            schedules = flexibleScheduleDaoList.map{
                scheduleDao ->
                FlexiblePersonalScheduleDto(
                    id = scheduleDao.scheduleId!!.toString(),
                    startTime = scheduleDao.startTime,
                    endTime = scheduleDao.endTime,
                    title = scheduleDao.title,
                    color = scheduleDao.color,
                    friends = scheduleDao.friends.map{friend -> friend.toString()}
                )
            }
        )
    }
}
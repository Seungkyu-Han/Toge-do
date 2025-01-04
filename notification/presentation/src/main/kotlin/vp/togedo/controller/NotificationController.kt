package vp.togedo.controller

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import vp.togedo.data.sse.SSEDto
import vp.togedo.service.NotificationService


@RestController
@RequestMapping("/api/v1/notification")
class NotificationController(
    private val notificationService: NotificationService
) {

    @GetMapping(produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun subscribeNotifications(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
    ): Flux<ServerSentEvent<SSEDto>>{
        return notificationService.subscribeNotification(userId)
    }

}
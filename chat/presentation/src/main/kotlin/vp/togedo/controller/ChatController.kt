package vp.togedo.controller

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import vp.togedo.data.dto.MessageResDto
import vp.togedo.service.ChatService

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val chatService: ChatService,
) {

    @GetMapping("/chats/{groupId}")
    fun getChatMessages(
        @Parameter(hidden = true) @RequestHeader("X-VP-UserId") userId: String,
        @PathVariable groupId: String,
    ): ResponseEntity<Flux<MessageResDto>> =
        ResponseEntity.ok(
            chatService.getChatMessages(groupId)
        )
}
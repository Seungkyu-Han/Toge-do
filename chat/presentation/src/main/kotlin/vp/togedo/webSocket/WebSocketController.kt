package vp.togedo.webSocket

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import vp.togedo.data.dto.MessageReqDto
import vp.togedo.service.ChatService

@RestController
class WebSocketController(
    private val chatService: ChatService
){
    @MessageMapping("/{groupId}")
    fun publishChatMessage(
        simpMessageHeaderAccessor: SimpMessageHeaderAccessor,
        @DestinationVariable groupId: String,
        @RequestBody messageReqDto: MessageReqDto
    ): Mono<Void> {
        val userId = simpMessageHeaderAccessor.sessionAttributes?.get("userId") as String?
            ?: throw IllegalArgumentException()

        return chatService.publishMessage(
            groupId = groupId,
            userId = userId,
            message = messageReqDto.message
        ).then()
    }

}
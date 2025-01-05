package vp.togedo.controller

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import vp.togedo.data.dto.MessageReqDto
import vp.togedo.security.config.JwtTokenProvider
import vp.togedo.service.ChatService

@RestController
class WebSocketController(
    private val chatService: ChatService,
    private val jwtTokenProvider: JwtTokenProvider
){
    @MessageMapping("/{groupId}")
    fun publishChatMessage(
        @Parameter(hidden = true) @Header(HttpHeaders.AUTHORIZATION) accessToken: String,
        @DestinationVariable groupId: String,
        @RequestBody messageReqDto: MessageReqDto
    ): Mono<ResponseEntity<Void>> {
        return chatService.publishMessage(
            groupId = groupId,
            userId = jwtTokenProvider.getUserId(accessToken.removePrefix("Bearer "))!!,
            message = messageReqDto.message
        ).then(Mono.fromCallable { ResponseEntity(HttpStatus.OK) })
    }

}
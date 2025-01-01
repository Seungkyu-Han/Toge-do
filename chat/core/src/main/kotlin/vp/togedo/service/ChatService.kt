package vp.togedo.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dto.MessageResDto

interface ChatService {

    fun getChatMessages(groupId: String): Flux<MessageResDto>

    fun publishMessage(
        groupId: String,
        userId: String,
        message: String
    ): Mono<Void>
}
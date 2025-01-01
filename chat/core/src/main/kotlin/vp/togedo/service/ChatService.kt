package vp.togedo.service

import reactor.core.publisher.Flux
import vp.togedo.data.dto.MessageResDto

interface ChatService {

    fun getChatMessages(groupId: String): Flux<MessageResDto>
}
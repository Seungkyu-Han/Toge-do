package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import vp.togedo.data.dto.MessageResDto
import vp.togedo.document.ChatDocument
import vp.togedo.repository.ChatRepository
import vp.togedo.service.ChatService

@Service
class ChatServiceImpl(
    private val chatRepository: ChatRepository
): ChatService {

    override fun getChatMessages(groupId: String): Flux<MessageResDto> {
        return chatRepository.findByGroupId(ObjectId(groupId))
            .map(::messageDocumentToDto)
    }

    private fun messageDocumentToDto(chatDocument: ChatDocument): MessageResDto =
        MessageResDto(
            message = chatDocument.message,
            senderId = chatDocument.senderId.toString(),
            createdAt = chatDocument.createdAt
        )
}
package vp.togedo.service.impl

import com.mongodb.client.model.changestream.OperationType
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import vp.togedo.data.dto.MessageResDto
import vp.togedo.document.ChatDocument
import vp.togedo.repository.ChatRepository
import vp.togedo.service.ChatService

@Service
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    reactiveMongoTemplate: ReactiveMongoTemplate,
    private val simpMessageSendingOperations: SimpMessageSendingOperations
): ChatService {

    init{
        reactiveMongoTemplate.changeStream(ChatDocument::class.java)
            .listen()
            .doOnNext{
                item ->
                val chatDocument = item.body
                val operationType = item.operationType

                if(chatDocument != null && operationType == OperationType.INSERT) {
                    println(chatDocument)
                    simpMessageSendingOperations.convertAndSend(
                        "/chat-sub/${chatDocument.groupId}", messageDocumentToDto(chatDocument)
                    )
                }
            }.subscribe()
    }

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
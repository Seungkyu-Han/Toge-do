package vp.togedo.config.webSocket

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageDeliveryException
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler
import vp.togedo.data.dto.WebSocketEventDto
import vp.togedo.data.enums.WebSocketEventEnum
import java.nio.charset.StandardCharsets

@Configuration
class StompErrorHandler(
    private val objectMapper: ObjectMapper
): StompSubProtocolErrorHandler(){

    override fun handleClientMessageProcessingError(
        clientMessage: Message<ByteArray>?,
        ex: Throwable
    ): Message<ByteArray>? {

        if (ex is MessageDeliveryException && ex.cause is AuthException){
            val webSocketEventEnum = WebSocketEventEnum.AUTHORIZATION_ERROR

            val stompHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR)

            stompHeaderAccessor.message = "Fail to send message"

            val errorBody = objectMapper.writeValueAsString(
                WebSocketEventDto(state = webSocketEventEnum.value, message = webSocketEventEnum.message)
            ).toByteArray(StandardCharsets.UTF_8)

            return MessageBuilder.createMessage(errorBody, stompHeaderAccessor.messageHeaders)
        }
        return super.handleClientMessageProcessingError(clientMessage, ex)
    }
}
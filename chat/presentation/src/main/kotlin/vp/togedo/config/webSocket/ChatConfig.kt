package vp.togedo.config.webSocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class ChatConfig(
    private val stompErrorHandler: StompErrorHandler,
    private val filterChannelInterceptor: FilterChannelInterceptor
): WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket/v1/chat")
            .setAllowedOriginPatterns("*")
            .withSockJS()
        registry.addEndpoint("/websocket/v1/chat")
            .setAllowedOriginPatterns("*")
        registry.setErrorHandler(stompErrorHandler)
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/sub")
        registry.setApplicationDestinationPrefixes("/pub")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(filterChannelInterceptor)
    }
}
package vp.togedo.config.webSocket

import org.springframework.http.HttpHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import vp.togedo.security.config.JwtTokenProvider
import vp.togedo.security.util.HeaderUtil

@Component
class FilterChannelInterceptor(
    private val headerUtil: HeaderUtil = HeaderUtil(),
    private val jwtTokenProvider: JwtTokenProvider
): ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val stompHeaderAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (stompHeaderAccessor!!.command == StompCommand.CONNECT) {
            try{
                val bearerToken = stompHeaderAccessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
                val token = headerUtil.extractAccessTokenFromHeader(bearerToken!!)
                val userId = jwtTokenProvider.getUserId(token)
                stompHeaderAccessor.sessionAttributes?.put("userId", userId)
            }catch(e: Exception){
                throw AuthException()
            }
        }
        return message
    }
}
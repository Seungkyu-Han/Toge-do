package vp.togedo.service

import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Flux
import vp.togedo.data.sse.SSEDao
import vp.togedo.data.sse.SSEDto

interface NotificationService {

    fun subscribeNotification(id: String): Flux<ServerSentEvent<SSEDto>>

    fun publishNotification(id: String, sseDao: SSEDao): Boolean
}
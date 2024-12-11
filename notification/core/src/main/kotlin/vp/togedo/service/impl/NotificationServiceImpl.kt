package vp.togedo.service.impl

import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many
import vp.togedo.data.notification.SSEDao
import vp.togedo.data.notification.SSEDto
import vp.togedo.service.NotificationService
import java.util.concurrent.ConcurrentHashMap

@Service
class NotificationServiceImpl: NotificationService {

    private val sinkMap = ConcurrentHashMap<String, Many<SSEDao>>()

    override fun subscribeNotification(id: String): Flux<ServerSentEvent<SSEDto>> {
        sinkMap[id] = Sinks.many().unicast().onBackpressureBuffer()
        return sinkMap[id]!!.asFlux()
            .doOnCancel { sinkMap.remove(id) }
            .map{
                sseDao ->
                ServerSentEvent
                    .builder(
                        SSEDto(
                            sender = sseDao.sender
                        )
                    )
                    .event(sseDao.event.eventValue.toString())
                    .build()
            }
    }

    override fun publishNotification(id: String, sseDao: SSEDao): Boolean {
        val sink = sinkMap[id]
        return if (sink != null) {
            sink.tryEmitNext(sseDao)
            true
        } else {
            false
        }
    }
}
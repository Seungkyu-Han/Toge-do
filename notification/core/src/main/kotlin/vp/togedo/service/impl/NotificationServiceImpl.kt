package vp.togedo.service.impl

import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.Many
import vp.togedo.data.sse.SSEDao
import vp.togedo.data.sse.SSEDto
import vp.togedo.service.NotificationService
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class NotificationServiceImpl: NotificationService {

    private val sinkMap = ConcurrentHashMap<String, Many<SSEDao>>()

    override fun subscribeNotification(id: String): Flux<ServerSentEvent<SSEDto>> {
        sinkMap[id] = Sinks.many().unicast().onBackpressureBuffer()

        val notificationFlux = sinkMap[id]!!.asFlux()
            .doOnCancel { sinkMap.remove(id) }
            .map { sseDao ->
                ServerSentEvent.builder(
                    SSEDto(
                        state = sseDao.event.eventValue,
                        sender = sseDao.sender,
                        image = sseDao.image,
                    )
                )
                    .event("message")
                    .build()
            }

        val keepAliveFlux = Flux.interval(Duration.ofSeconds(30))
            .map {
                ServerSentEvent.builder<SSEDto>()
                    .comment("keep-alive")
                    .build()
            }

        return Flux.merge(notificationFlux, keepAliveFlux)
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
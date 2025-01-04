package vp.togedo.kafka.service

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.groupSchedule.SuggestGroupScheduleEventDto

interface GroupScheduleKafkaService {

    fun publishSuggestConfirmScheduleEvent(suggestGroupScheduleEventDto: SuggestGroupScheduleEventDto): Mono<Void>

    fun publishConfirmScheduleEvent(confirmScheduleEvent: SuggestGroupScheduleEventDto): Mono<Void>
}
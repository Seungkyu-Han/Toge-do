package vp.togedo.kafka.service

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.groupSchedule.ConfirmScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.CreateGroupScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.SuggestGroupScheduleEventDto

interface GroupScheduleKafkaService {

    fun publishSuggestConfirmScheduleEvent(suggestGroupScheduleEventDto: SuggestGroupScheduleEventDto): Mono<Void>

    fun publishConfirmScheduleEvent(confirmScheduleEvent: ConfirmScheduleEventDto): Mono<Void>

    fun publishCreateGroupScheduleEvent(createGroupScheduleEventDto: CreateGroupScheduleEventDto): Mono<Void>
}
package vp.togedo.kafka.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.data.groupSchedule.ConfirmScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.CreateGroupScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.SuggestGroupScheduleEventDto
import vp.togedo.kafka.publish.KafkaPublisher
import vp.togedo.kafka.service.GroupScheduleKafkaService

@Service
class GroupScheduleKafkaServiceImpl(
    private val kafkaPublisher: KafkaPublisher
): GroupScheduleKafkaService {

    override fun publishSuggestConfirmScheduleEvent(suggestGroupScheduleEventDto: SuggestGroupScheduleEventDto): Mono<Void> {
        return kafkaPublisher.publishKafkaEvent(
            EventEnums.SUGGEST_CONFIRM_SCHEDULE,
            suggestGroupScheduleEventDto
        )
    }

    override fun publishConfirmScheduleEvent(confirmScheduleEvent: ConfirmScheduleEventDto): Mono<Void> {
        return kafkaPublisher.publishKafkaEvent(
            EventEnums.CONFIRM_SCHEDULE,
            confirmScheduleEvent
        )
    }

    override fun publishCreateGroupScheduleEvent(createGroupScheduleEventDto: CreateGroupScheduleEventDto): Mono<Void> {
        return kafkaPublisher.publishKafkaEvent(
            EventEnums.CREATE_GROUP_SCHEDULE,
            createGroupScheduleEventDto
        )
    }
}
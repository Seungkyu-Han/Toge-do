package vp.togedo.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.kafka.data.groupSchedule.ConfirmScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.CreateGroupScheduleEventDto
import vp.togedo.kafka.data.groupSchedule.SuggestGroupScheduleEventDto
import vp.togedo.data.sse.EventEnums
import vp.togedo.data.sse.SSEDao
import vp.togedo.kafka.config.Topics
import vp.togedo.service.FCMService
import vp.togedo.service.NotificationService

@Component
class GroupScheduleEventListener(
    private val notificationService: NotificationService,
    private val fcmService: FCMService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = [Topics.CREATE_GROUP_SCHEDULE], groupId = "seungkyu")
    fun createGroupSchedule(message: String){
        val event = EventEnums.CREATE_GROUP_SCHEDULE
        val createGroupScheduleEventDto = objectMapper.readValue(message, CreateGroupScheduleEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = createGroupScheduleEventDto.receiverId,
            sseDao = SSEDao(event, createGroupScheduleEventDto.name, null)
        )
        if (!isSSE){
            fcmService.pushNotification(
                userId = createGroupScheduleEventDto.receiverId,
                title = event.eventTitle,
                content = "${createGroupScheduleEventDto.name}${event.eventContent}",
                image = null
            )
        }
    }

    @KafkaListener(topics = [Topics.SUGGEST_CONFIRM_SCHEDULE], groupId = "seungkyu")
    fun suggestConfirmSchedule(message: String){
        val event = EventEnums.SUGGEST_CONFIRM_SCHEDULE
        val suggestGroupScheduleEventDto = objectMapper.readValue(message, SuggestGroupScheduleEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = suggestGroupScheduleEventDto.receiverId,
            sseDao = SSEDao(event, suggestGroupScheduleEventDto.name, null)
        )
        if (!isSSE){
            fcmService.pushNotification(
                userId = suggestGroupScheduleEventDto.receiverId,
                title = event.eventTitle,
                content = "${suggestGroupScheduleEventDto.name}${event.eventContent}",
                image = null
            )
        }
    }

    @KafkaListener(topics = [Topics.CONFIRM_SCHEDULE], groupId = "seungkyu")
    fun confirmSchedule(message: String){
        val event = EventEnums.CONFIRM_SCHEDULE
        val confirmSchedule = objectMapper.readValue(message, ConfirmScheduleEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = confirmSchedule.receiverId,
            sseDao = SSEDao(event, confirmSchedule.name, null)
        )
        if(!isSSE){
            fcmService.pushNotification(
                userId = confirmSchedule.receiverId,
                title = event.eventTitle,
                content = "${confirmSchedule.name}${event.eventContent}",
                image = null
            )
        }
    }

}
package vp.togedo.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.data.groupSchedule.CreateGroupScheduleEventDto
import vp.togedo.data.groupSchedule.SuggestGroupScheduleEventDto
import vp.togedo.data.notification.EventEnums
import vp.togedo.data.notification.SSEDao
import vp.togedo.service.FCMService
import vp.togedo.service.NotificationService

@Component
class GroupScheduleEventListener(
    private val notificationService: NotificationService,
    private val fcmService: FCMService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["CREATE_GROUP_SCHEDULE_TOPIC"], groupId = "seungkyu")
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

    @KafkaListener(topics = ["SUGGEST_CONFIRM_SCHEDULE_TOPIC"], groupId = "seungkyu")
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

}
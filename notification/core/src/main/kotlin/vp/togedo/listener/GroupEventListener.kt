package vp.togedo.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.data.group.InviteGroupEventDto
import vp.togedo.data.notification.EventEnums
import vp.togedo.data.notification.SSEDao
import vp.togedo.service.FCMService
import vp.togedo.service.NotificationService

@Component
class GroupEventListener(
    private val notificationService: NotificationService,
    private val fcmService: FCMService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["INVITE_GROUP_TOPIC"], groupId = "seungkyu")
    fun inviteGroup(message: String){
        val event = EventEnums.INVITE_GROUP
        val inviteGroupEventDto = objectMapper.readValue(message, InviteGroupEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = inviteGroupEventDto.receiverId,
            sseDao = SSEDao(event, inviteGroupEventDto.name, null)
        )
        if(!isSSE){
            fcmService.pushNotification(
                userId = inviteGroupEventDto.receiverId,
                title = event.eventTitle,
                content = "${inviteGroupEventDto.name}${event.eventContent}",
                image = null
            )
        }
    }
}
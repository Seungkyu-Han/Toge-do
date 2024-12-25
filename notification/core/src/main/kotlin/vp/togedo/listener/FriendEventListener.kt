package vp.togedo.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.data.notification.EventEnums
import vp.togedo.data.notification.FriendApproveEventDto
import vp.togedo.data.notification.FriendRequestEventDto
import vp.togedo.data.notification.SSEDao
import vp.togedo.service.FCMService
import vp.togedo.service.NotificationService

@Component
class FriendEventListener(
    private val notificationService: NotificationService,
    private val fcmService: FCMService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(topics = ["FRIEND_REQUEST_TOPIC"], groupId = "seungkyu")
    fun requestFriend(message: String){
        val event = EventEnums.REQUEST_FRIEND_EVENT
        val friendRequestEventDto = objectMapper.readValue(message, FriendRequestEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = friendRequestEventDto.receiverId,
            sseDao = SSEDao(event, friendRequestEventDto.sender, friendRequestEventDto.image)
        )
        if (!isSSE && friendRequestEventDto.deviceToken != null){
            fcmService.pushNotification(
                userId = friendRequestEventDto.receiverId,
                title = event.eventTitle,
                content = "${friendRequestEventDto.sender}${event.eventContent}",
                image = friendRequestEventDto.image
            )
        }
    }

    @KafkaListener(topics = ["FRIEND_APPROVE_TOPIC"], groupId = "seungkyu")
    fun approveFriend(message: String){
        val event = EventEnums.APPROVE_FRIEND_EVENT
        val friendApproveEventDto = objectMapper.readValue(message, FriendApproveEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = friendApproveEventDto.receiverId,
            sseDao = SSEDao(event, friendApproveEventDto.sender, friendApproveEventDto.image)
        )
        if (!isSSE && friendApproveEventDto.deviceToken != null){
            fcmService.pushNotification(
                userId = friendApproveEventDto.receiverId,
                title = event.eventTitle,
                content = "${friendApproveEventDto.sender}${event.eventContent}",
                image = friendApproveEventDto.image
            )
        }
    }
}
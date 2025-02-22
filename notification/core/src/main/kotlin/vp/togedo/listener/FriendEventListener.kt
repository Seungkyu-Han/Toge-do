package vp.togedo.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.data.friend.FriendApproveEventDto
import vp.togedo.kafka.data.friend.FriendRequestEventDto
import vp.togedo.data.sse.SSEDao
import vp.togedo.kafka.config.Topics
import vp.togedo.service.FCMService
import vp.togedo.service.NotificationService

@Component
class FriendEventListener(
    private val notificationService: NotificationService,
    private val fcmService: FCMService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(topics = [Topics.FRIEND_REQUEST], groupId = "seungkyu")
    fun requestFriend(message: String){
        val event = EventEnums.REQUEST_FRIEND
        val friendRequestEventDto = objectMapper.readValue(message, FriendRequestEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = friendRequestEventDto.receiverId,
            sseDao = SSEDao(event, friendRequestEventDto.sender, friendRequestEventDto.image)
        )
        if (!isSSE){
            fcmService.pushNotification(
                userId = friendRequestEventDto.receiverId,
                title = event.eventTitle,
                content = "${friendRequestEventDto.sender}${event.eventContent}",
                image = friendRequestEventDto.image
            )
        }
    }

    @KafkaListener(topics = [Topics.FRIEND_APPROVE], groupId = "seungkyu")
    fun approveFriend(message: String){
        val event = EventEnums.APPROVE_FRIEND
        val friendApproveEventDto = objectMapper.readValue(message, FriendApproveEventDto::class.java)
        val isSSE = notificationService.publishNotification(
            id = friendApproveEventDto.receiverId,
            sseDao = SSEDao(event, friendApproveEventDto.sender, friendApproveEventDto.image)
        )
        if (!isSSE){
            fcmService.pushNotification(
                userId = friendApproveEventDto.receiverId,
                title = event.eventTitle,
                content = "${friendApproveEventDto.sender}${event.eventContent}",
                image = friendApproveEventDto.image
            )
        }
    }
}
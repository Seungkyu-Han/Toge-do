package vp.togedo.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.data.notification.EventEnums
import vp.togedo.data.notification.FriendApproveEventDto
import vp.togedo.data.notification.FriendRequestEventDto
import vp.togedo.data.notification.SSEDao
import vp.togedo.service.NotificationService

@Component
class FriendEventListener(
    private val notificationService: NotificationService,
    private val objectMapper: ObjectMapper,
) {

    @KafkaListener(topics = ["FRIEND_REQUEST_TOPIC"], groupId = "seungkyu")
    fun requestFriend(message: String){
        val friendRequestEventDto = objectMapper.readValue(message, FriendRequestEventDto::class.java)
        notificationService.publishNotification(
            id = friendRequestEventDto.receiverId,
            sseDao = SSEDao(EventEnums.REQUEST_FRIEND_EVENT, friendRequestEventDto.sender)
        )
    }

    @KafkaListener(topics = ["FRIEND_APPROVE_TOPIC"], groupId = "seungkyu")
    fun approveFriend(message: String){
        val friendApproveEventDto = objectMapper.readValue(message, FriendApproveEventDto::class.java)
        notificationService.publishNotification(
            id = friendApproveEventDto.receiverId,
            sseDao = SSEDao(EventEnums.APPROVE_FRIEND_EVENT, friendApproveEventDto.sender)
        )
    }
}
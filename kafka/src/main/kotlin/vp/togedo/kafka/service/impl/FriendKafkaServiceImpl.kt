package vp.togedo.kafka.service.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.kafka.data.enums.EventEnums
import vp.togedo.kafka.data.friend.FriendApproveEventDto
import vp.togedo.kafka.data.friend.FriendRequestEventDto
import vp.togedo.kafka.publish.KafkaPublisher
import vp.togedo.kafka.service.FriendKafkaService

@Service
class FriendKafkaServiceImpl(
    private val kafkaPublisher: KafkaPublisher
): FriendKafkaService {

    override fun publishRequestFriendEvent(friendRequestEventDto: FriendRequestEventDto): Mono<Void> {
        return kafkaPublisher.publishKafkaEvent(
            EventEnums.REQUEST_FRIEND,
            friendRequestEventDto,
        )
    }

    override fun publishApproveFriendEvent(friendApproveEventDto: FriendApproveEventDto): Mono<Void> {
        return kafkaPublisher.publishKafkaEvent(
            EventEnums.APPROVE_FRIEND,
            friendApproveEventDto,
        )
    }
}
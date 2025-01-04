package vp.togedo.kafka.service

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.friend.FriendApproveEventDto
import vp.togedo.kafka.data.friend.FriendRequestEventDto

interface FriendKafkaService {

    fun publishRequestFriendEvent(friendRequestEventDto: FriendRequestEventDto): Mono<Void>

    fun publishApproveFriendEvent(friendApproveEventDto: FriendApproveEventDto): Mono<Void>
}
package vp.togedo.kafka.service

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.group.InviteGroupEventDto
import vp.togedo.kafka.data.groupSchedule.CreateGroupScheduleEventDto

interface GroupKafkaService {

    fun publishInviteGroupEvent(inviteGroupEventDto: InviteGroupEventDto): Mono<Void>

    fun publishCreateGroupScheduleEvent(createGroupScheduleEventDto: CreateGroupScheduleEventDto): Mono<Void>
}
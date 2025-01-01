package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.data.dao.group.GroupDao
import vp.togedo.data.dao.groupSchedule.GroupScheduleDao

interface KafkaService {

    fun publishInviteGroupEvent(receiverId: ObjectId, group: GroupDao): Mono<Void>

    fun publishCreateGroupScheduleEvent(receiverId: ObjectId, groupSchedule: GroupScheduleDao): Mono<Void>

    fun publishSuggestConfirmScheduleEvent(receiverId: String, groupSchedule: GroupScheduleDao): Mono<Void>

    fun publishConfirmScheduleEvent(receiverId: String, groupSchedule: GroupScheduleDao): Mono<Void>
}
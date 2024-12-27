package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dto.groupSchedule.GroupScheduleDetailDto
import vp.togedo.data.dto.groupSchedule.GroupScheduleDto

interface GroupScheduleConnector {

    fun createGroupSchedule(
        userId: ObjectId,
        groupId: ObjectId,
        name: String,
        startDate: Long,
        endDate: Long,
    ): Mono<GroupScheduleDetailDto>

    fun readGroupSchedules(groupId: ObjectId): Flux<GroupScheduleDto>

    fun readGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<GroupScheduleDetailDto>
}
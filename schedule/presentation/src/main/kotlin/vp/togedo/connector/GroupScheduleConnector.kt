package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Mono
import vp.togedo.data.dto.groupSchedule.GroupScheduleDetailDto
import java.time.LocalDate

interface GroupScheduleConnector {

    fun createGroupSchedule(
        userId: ObjectId,
        groupId: ObjectId,
        name: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Mono<GroupScheduleDetailDto>
}
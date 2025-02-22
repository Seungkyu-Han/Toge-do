package vp.togedo.connector

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dao.groupSchedule.IndividualScheduleDao
import vp.togedo.data.dao.groupSchedule.IndividualScheduleListDao
import vp.togedo.data.dto.groupSchedule.GroupScheduleDetailDto
import vp.togedo.data.dto.groupSchedule.GroupScheduleDto
import vp.togedo.data.dto.groupSchedule.UpdateGroupScheduleReqDto

interface GroupScheduleConnector {

    fun createGroupSchedule(
        userId: ObjectId,
        groupId: ObjectId,
        name: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
    ): Mono<GroupScheduleDetailDto>

    fun readGroupSchedules(groupId: ObjectId): Flux<GroupScheduleDto>

    fun readGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<GroupScheduleDetailDto>

    fun updateGroupSchedule(updateGroupScheduleReqDto: UpdateGroupScheduleReqDto): Mono<GroupScheduleDetailDto>

    fun deleteGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<Void>

    fun createPersonalScheduleInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao>

    fun updatePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao>

    fun deletePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleIdList: List<ObjectId>
    ): Mono<IndividualScheduleDao>

    fun createSuggestGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        startTime: String,
        endTime: String,
    ): Mono<GroupScheduleDetailDto>

    fun acceptConfirmGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId
    ): Mono<GroupScheduleDetailDto>

    fun rejectConfirmGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId
    ): Mono<GroupScheduleDetailDto>
}
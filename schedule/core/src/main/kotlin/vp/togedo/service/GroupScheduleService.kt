package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dao.groupSchedule.*

interface GroupScheduleService {

    /**
     * 공유 일정을 생성하는 메서드
     * @param groupId 공유 일정을 생성할 그룹의 object id
     * @param name 생성할 공유 일정의 이름
     * @param startDate 희망 공유 일정일의 시작일
     * @param endDate 희망 공유 일정일의 종료일
     * @param startTime 공유 일정의 시작 시간
     * @param endTime 공유 일정의 종료 시간
     * @return 생성된 공유 일정의 dao
     */
    fun createGroupSchedule(groupId: ObjectId, name: String, startDate: String, endDate: String, startTime: String, endTime: String): Mono<GroupScheduleDao>

    /**
     * 해당 그룹의 공유 일정 목록을 가져오는 메서드
     * @param groupId 일정 목록을 가져올 그룹의 object id
     * @return 해당 그룹의 공유 일정 목록(멤버들의 스케줄 제외)
     */
    fun readGroupSchedules(groupId: ObjectId): Flux<GroupScheduleDao>

    /**
     * 해당 공유 일정의 자세한 정보를 조회하는 메서드
     * @param groupId 일정 목록을 가져올 그룹의 object id
     * @param scheduleId 해당 일정에서 정보를 가져올 공유 일정의 object id
     * @return 해당 그룹의 공유 일정 목록(멤버들의 스케줄 제외)
     */
    fun readGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<GroupScheduleDao>

    /**
     * 해당 공유 일정을 수정하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param groupScheduleDao 수정할 공유 일정의 dao
     * @return 수정된 group schedule dao
     */
    fun updateGroupSchedule(groupId: ObjectId, groupScheduleDao: GroupScheduleDao): Mono<GroupScheduleDao>

    /**
     * 해당 공유 일정을 삭제하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param scheduleId 삭제하려는 공유 일정의 object id
     * @return Mono void
     */
    fun deleteGroupSchedule(groupId: ObjectId, scheduleId: ObjectId): Mono<Void>

    /**
     * 공유 일정에 본인의 일정을 등록하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param scheduleId 일정을 등록할 공유 일정의 object id
     * @param userId 등록할 유저의 object id
     * @param individualScheduleListDao 등록할 개인 일정들
     * @return 수정된 group schedule dao
     */
    fun addPersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao>

    /**
     * 공유 일정에 본인의 일정을 수정하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param scheduleId 일정을 수정할 공유 일정의 object id
     * @param userId 수정할 유저의 object id
     * @param individualScheduleListDao 수정할 개인 일정들
     * @return 수정된 group schedule dao
     */
    fun updatePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleListDao: IndividualScheduleListDao
    ): Mono<IndividualScheduleDao>

    /**
     * 공유 일정에서 본인의 일정을 삭제하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param scheduleId 일정을 삭제할 공유 일정의 object id
     * @param userId 일정을 삭제할 유저의 object id
     * @param individualScheduleIdList 일정을 삭제할 공유 일정 속 개인 일정의 object id 리스트
     * @return 삭제된 group schedule dao
     */
    fun deletePersonalSchedulesInGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        individualScheduleIdList: List<ObjectId>
    ): Mono<IndividualScheduleDao>

    /**
     * 공유 일정 상태를 변경하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param scheduleId 확정할 일정의 object id
     * @param userId 확정을 요청하는 유저의 object id
     * @param confirmScheduleDao 확정을 요청하는 스케줄의 dao
     * @return 변경된 일정의 group schedule dao
     */
    fun changeStateToConfirmSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
        confirmScheduleDao: ConfirmScheduleDao
    ): Mono<GroupScheduleDao>

    /**
     * 일정 확인 요청을 수락하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param scheduleId 수락할 일정의 object id
     * @param userId 수락하는 유저의 object id
     * @return 변경된 일정의 group schedule dao
     */
    fun acceptConfirmGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId,
    ): Mono<GroupScheduleDao>

    /**
     * 일정 확인 요청을 거절하는 메서드
     * @param groupId 해당 그룹의 object id
     * @param scheduleId 거절할 일정의 object id
     * @return 변경된 일정의 group schedule dao
     */
    fun rejectConfirmGroupSchedule(
        groupId: ObjectId,
        scheduleId: ObjectId,
        userId: ObjectId
    ): Mono<GroupScheduleDao>
}
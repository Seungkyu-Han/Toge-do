package vp.togedo.service

import org.bson.types.ObjectId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import vp.togedo.data.dao.groupSchedule.GroupScheduleDao

interface GroupScheduleService {

    /**
     * 공유 일정을 생성하는 메서드
     * @param groupId 공유 일정을 생성할 그룹의 object id
     * @param name 생성할 공유 일정의 이름
     * @param startDate 희망 공유 일정일의 시작일
     * @param endDate 희망 공유 일정일의 종료일
     * @return 생성된 공유 일정의 dao
     */
    fun createGroupSchedule(groupId: ObjectId, name: String, startDate: Long, endDate: Long): Mono<GroupScheduleDao>

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
     * @param groupScheduleDao 수정할 group schedule의 dao
     * @return 수정된 group schedule dao
     */
    fun updateGroupSchedule(groupId: ObjectId, groupScheduleDao: GroupScheduleDao): Mono<GroupScheduleDao>
}
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
}
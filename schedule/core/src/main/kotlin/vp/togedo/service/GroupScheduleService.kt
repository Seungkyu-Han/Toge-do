package vp.togedo.service

import org.bson.types.ObjectId
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
}
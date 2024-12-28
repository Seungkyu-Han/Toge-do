package vp.togedo.document

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import vp.togedo.enums.GroupScheduleStateEnum
import vp.togedo.util.exception.group.AlreadyJoinedGroupException
import vp.togedo.util.exception.groupSchedule.CantCreateMoreScheduleException
import vp.togedo.util.exception.group.NotJoinedGroupException
import vp.togedo.util.exception.groupSchedule.NotFoundGroupScheduleException

@Document(collection = "groups")
data class GroupDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId = ObjectId.get(),

    @JsonProperty("name")
    var name: String,

    @JsonProperty("members")
    val members: MutableSet<ObjectId> = mutableSetOf(),

    @JsonProperty("groupSchedules")
    val groupSchedules: MutableList<GroupSchedule> = mutableListOf()
){
    fun changeName(name: String): Mono<GroupDocument> {
        return Mono.fromCallable {
            this.name = name
            this
        }
    }

    fun addMember(id: ObjectId): Mono<GroupDocument>{
        return Mono.fromCallable {
            if (this.members.contains(id))
                throw AlreadyJoinedGroupException("이미 포함된 그룹입니다.")
            this.members.add(id)
            this
        }
    }

    fun removeMember(id: ObjectId): Mono<GroupDocument>{
        return Mono.fromCallable {
            if(!this.members.remove(id))
                throw NotJoinedGroupException("포함되지 않은 그룹입니다.")
            this
        }
    }

    /**
     * 그룹에 공유 일정을 추가하는 메서드
     * @param name 공유 일정의 이름
     * @param startDate 해당 일정의 가능한 희망 시작일
     * @param endDate 해당 일정의 가능한 희망 종료일
     */
    fun createGroupSchedule(
        name: String,
        startDate: Long,
        endDate: Long,
    ): Mono<GroupDocument>{
        return Mono.fromCallable {

            if (this.groupSchedules.size >= 100)
                throw CantCreateMoreScheduleException("해당 그룹에서 생성 가능한 스케줄을 초과합니다.")

            val groupSchedule = GroupSchedule(
                name = name,
                startDate = startDate,
                endDate = endDate,
                personalScheduleMap = this.members.associateWith{PersonalSchedules()}.toMutableMap()
            )

            this.groupSchedules.add(groupSchedule)

            this
        }
    }

    /**
     * 해당 object id를 가지는 공유 일정을 찾는 메서드
     * @param scheduleId 찾을 공유 일정의 object id
     * @return 찾으려는 공유 일정
     * @throws NotFoundGroupScheduleException 해당 공유일정을 찾을 수 없음
     */
    fun findGroupScheduleById(
        scheduleId: ObjectId
    ): Mono<GroupSchedule>{
        return Mono.fromCallable {
            groupSchedules.find { it.id == scheduleId } ?: throw NotFoundGroupScheduleException("해당 공유 일정이 존재하지 않습니다.")
        }
    }

    /**
     * 해당 공유 일정을 수정하는 메서드
     * @param scheduleId 해당 스케줄의 object id
     * @param name 수정할 name
     * @param startDate 수정할 startDate
     * @param endDate 수정할 endDate
     * @return 변경된 group schedule
     * @throws NotFoundGroupScheduleException 해당 공유일정을 찾을 수 없음
     */
    fun updateGroupSchedule(
        scheduleId: ObjectId,
        name: String,
        startDate: Long,
        endDate: Long
    ): Mono<GroupSchedule>{
        return Mono.fromCallable {
            val index: Int = groupSchedules.indexOfFirst { it.id == scheduleId }

            if(index == -1)
                throw NotFoundGroupScheduleException("해당 공유 일정이 존재하지 않습니다.")

            this.groupSchedules[index] = this.groupSchedules[index].copy(
                name = name,
                startDate = startDate,
                endDate = endDate
            )

            this.groupSchedules[index]
        }
    }

    /**
     * 해당 공유 일정을 삭제하는 메서드
     * @param scheduleId 삭제하려는 공유 일정의 object id
     * @return 변경된 group document
     * @throws NotFoundGroupScheduleException 해당 공유 일정을 찾을 수 없음
     */
    fun deleteGroupScheduleById(
        scheduleId: ObjectId
    ): Mono<GroupDocument>{
        return Mono.fromCallable {
            if(!this.groupSchedules.removeIf{it.id == scheduleId})
                throw NotFoundGroupScheduleException("해당 공유 일정이 존재하지 않습니다.")

            this
        }
    }
}

data class GroupSchedule(
    @JsonProperty("id")
    val id: ObjectId = ObjectId.get(),

    @JsonProperty("name")
    var name: String,

    @JsonProperty("startDate")
    var startDate: Long,

    @JsonProperty("endDate")
    var endDate: Long,

    @JsonProperty("personalSchedules")
    val personalScheduleMap: MutableMap<ObjectId, PersonalSchedules>,

    @JsonProperty("state")
    var state: GroupScheduleStateEnum = GroupScheduleStateEnum.DISCUSSING,

    @JsonProperty("confirmedStartDate")
    var confirmedStartDate: String? = null,

    @JsonProperty("confirmedEndDate")
    var confirmedEndDate: String? = null
)

data class PersonalSchedules(
    @JsonProperty("personalSchedules")
    val personalSchedules: MutableList<PersonalSchedule> = mutableListOf()
)

data class PersonalSchedule(
    val id: ObjectId = ObjectId.get(),
    val startTime: Long,
    val endTime: Long
)
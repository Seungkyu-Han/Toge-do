package vp.togedo.document

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import reactor.core.publisher.Mono
import vp.togedo.enums.GroupScheduleStateEnum
import vp.togedo.util.exception.group.AlreadyJoinedGroupException
import vp.togedo.util.exception.group.CantCreateMoreScheduleException
import vp.togedo.util.exception.group.NotJoinedGroupException

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
    val startTime: Long,
    val endTime: Long
)
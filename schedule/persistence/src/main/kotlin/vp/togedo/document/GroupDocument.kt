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
import vp.togedo.util.exception.groupSchedule.NotFoundPersonalScheduleException
import vp.togedo.util.exception.schedule.ConflictScheduleException
import vp.togedo.util.exception.schedule.InvalidTimeException

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
        startTime: String,
        endTime: String
    ): Mono<GroupDocument>{
        return Mono.fromCallable {

            if (this.groupSchedules.size >= 100)
                throw CantCreateMoreScheduleException("해당 그룹에서 생성 가능한 스케줄을 초과합니다.")

            val groupSchedule = GroupSchedule(
                name = name,
                startDate = startDate,
                endDate = endDate,
                personalScheduleMap = this.members.associateWith{PersonalSchedules()}.toMutableMap(),
                startTime = startTime,
                endTime = endTime
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

    @JsonProperty("startTime")
    var startTime: String,

    @JsonProperty("endTime")
    var endTime: String,

    @JsonProperty("personalSchedules")
    val personalScheduleMap: MutableMap<ObjectId, PersonalSchedules>,

    @JsonProperty("state")
    var state: GroupScheduleStateEnum = GroupScheduleStateEnum.DISCUSSING,

    @JsonProperty("confirmedStartDate")
    var confirmedStartDate: String? = null,

    @JsonProperty("confirmedEndDate")
    var confirmedEndDate: String? = null
){
    fun findGroupScheduleByUserId(userId: ObjectId): PersonalSchedules{

        val personalSchedules = this.personalScheduleMap[userId]

        if(personalSchedules != null)
            return personalSchedules
        else{
            personalScheduleMap[userId] = PersonalSchedules()
            return personalScheduleMap[userId]!!
        }
    }
}

data class PersonalSchedules(
    @JsonProperty("personalSchedules")
    val personalSchedules: MutableList<PersonalSchedule> = mutableListOf()
){
    fun deletePersonalSchedulesById(personalScheduleIdList: List<ObjectId>): Mono<PersonalSchedules> {
        return Mono.fromCallable{
            personalScheduleIdList.forEach(::deletePersonalScheduleById)

            this
        }
    }

    private fun deletePersonalScheduleById(personalScheduleId: ObjectId){
        if(!this.personalSchedules.removeIf { it.id == personalScheduleId })
            throw NotFoundPersonalScheduleException("해당 스케줄이 존재하지 않습니다.")
    }

    fun addPersonalSchedules(personalScheduleList: List<PersonalSchedule>): Mono<PersonalSchedules>{
        return Mono.fromCallable {
            personalScheduleList.forEach {
                personalSchedule ->
                this.addPersonalSchedule(personalSchedule)
            }

            this
        }
    }

    fun updatePersonalSchedules(personalScheduleList: List<PersonalSchedule>): Mono<PersonalSchedules>{
        return Mono.fromCallable {
            personalScheduleList.forEach {
                personalSchedule ->
                this.updatePersonalSchedule(personalSchedule)
            }
            this
        }
    }

    private fun updatePersonalSchedule(personalSchedule: PersonalSchedule): PersonalSchedules{

        if(personalSchedules.removeIf { it.id == personalSchedule.id })
            return addPersonalSchedule(personalSchedule)
        else
            throw NotFoundPersonalScheduleException("해당 스케줄이 존재하지 않습니다.")

    }

    private fun addPersonalSchedule(personalSchedule: PersonalSchedule): PersonalSchedules {
        this.checkValidTime(personalSchedule.startTime, personalSchedule.endTime)

        val insertedIndex = getInsertedIndex(personalSchedule.startTime, personalSchedule.endTime)

        this.personalSchedules.add(insertedIndex, personalSchedule)

        return this
    }


    private fun getInsertedIndex(startTime: Long, endTime: Long): Int{
        val index = personalSchedules.binarySearch(0){
            personalSchedule -> personalSchedule.startTime.compareTo(startTime)
        }

        if (index >= 0)
            throw ConflictScheduleException("해당 시작시간에 시작하는 스케줄이 있습니다.")

        val insertedIndex = (index + 1) * -1

        if(insertedIndex > 0)
            if(personalSchedules[insertedIndex - 1].endTime >= startTime)
                throw ConflictScheduleException("전 스케줄이 종료되지 않았습니다.")

        if(insertedIndex < personalSchedules.size)
            if(personalSchedules[insertedIndex].startTime <= endTime)
                throw ConflictScheduleException("뒤 스케줄의 시작시간과 충돌합니다.")

        return insertedIndex
    }

    private fun checkValidTime(startTime: Long, endTime: Long): Boolean {
        if (startTime > endTime)
            throw InvalidTimeException("시작 시간이 종료 시간보다 뒤입니다.")

        this.checkTimeRange(startTime)

        this.checkTimeRange(endTime)

        return true
    }

    private fun checkTimeRange(time: Long): Boolean{
        if (time !in 10_01_01_00_00 .. 99_12_31_23_59)
            throw InvalidTimeException("시간 범위 밖입니다.")

        return true
    }
}

data class PersonalSchedule(
    val id: ObjectId = ObjectId.get(),
    val startTime: Long,
    val endTime: Long
)
package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import vp.togedo.model.exception.group.NotFoundMemberException
import vp.togedo.model.exception.group.ScheduleNotRequestedException

data class GroupScheduleElement(
    @JsonProperty("id")
    val id: ObjectId = ObjectId.get(),

    @JsonProperty("name")
    var name: String,

    @JsonProperty("startDate")
    var startDate: String,

    @JsonProperty("endDate")
    var endDate: String,

    @JsonProperty("startTime")
    var startTime: String,

    @JsonProperty("endTime")
    var endTime: String,

    @JsonProperty("state")
    var state: GroupScheduleStateEnum = GroupScheduleStateEnum.DISCUSSING,

    @JsonProperty("members")
    val members: MutableSet<ObjectId>,

    @JsonProperty("confirmedUser")
    var confirmedUser: MutableSet<ObjectId> = mutableSetOf(),

    @JsonProperty("confirmedStartDate")
    var confirmedStartDate: String? = null,

    @JsonProperty("confirmedEndDate")
    var confirmedEndDate: String? = null
){

    /**
     * group schedule element를 변경하는 메서드
     * @param name 변경할 이름
     * @param startDate 변경할 시작일
     * @param endDate 변경할 종료일
     * @param startTime 변경할 시작시간
     * @param endTime 변경할 종료시간
     * @return 변경된 group schedule element
     */
    fun updateGroupScheduleElement(
        name: String,
        startDate: String,
        endDate: String,
        startTime: String,
        endTime: String,
    ): GroupScheduleElement {

        this.name = name
        this.startDate = startDate
        this.endDate = endDate
        this.startTime = startTime
        this.endTime = endTime

        return this
    }

    /**
     * 일정 확인을 요청하는 메서드
     * @param confirmedEndDate 요청하는 확정 시간
     * @param confirmedStartDate 요청하는 종료 시간
     * @param userId 요청하는 유저의 object id
     * @return 변경된 group schedule element
     */
    fun requestConfirmSchedule(
        confirmedStartDate: String,
        confirmedEndDate: String,
        userId: ObjectId
    ): GroupScheduleElement {

        this.state = GroupScheduleStateEnum.REQUESTED
        this.confirmedStartDate = confirmedStartDate
        this.confirmedEndDate = confirmedEndDate
        this.confirmedUser = mutableSetOf(userId)

        return this
    }

    /**
     * 일정 요청을 확인하는 메서드
     * @param userId 확인하는 유저의 object id
     * @param isApprove 요청 일정의 승인 여부
     * @return 변경된 group schedule element
     * @throws ScheduleNotRequestedException 해당 스케줄이 확인 요청 상태가 아님
     * @throws NotFoundMemberException 해당 일정의 멤버가 아님
     */
    fun checkRequestedSchedule(
        userId: ObjectId,
        isApprove: Boolean
    ): GroupScheduleElement {

        if(userId !in members)
            throw NotFoundMemberException()

        if(!(state == GroupScheduleStateEnum.REQUESTED || state == GroupScheduleStateEnum.CONFIRMED))
            throw ScheduleNotRequestedException()

        if (isApprove) {
            confirmedUser.add(userId)
            if (confirmedUser.size == members.size)
                this.state = GroupScheduleStateEnum.CONFIRMED
        }else{
            confirmedUser.clear()
            this.state = GroupScheduleStateEnum.REJECTED
        }
        return this
    }
}
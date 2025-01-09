package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.group.AlreadyMemberException
import vp.togedo.model.exception.group.NotFoundMemberException

@Document("individual_schedules")
data class IndividualScheduleDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("individualScheduleMap")
    val individualScheduleMap: MutableMap<ObjectId, IndividualScheduleList>
){
    /**
     * 해당 개인 일정 목록에 멤버를 추가하는 메서드
     * @param userId 추가하려는 유저의 object id
     * @return [IndividualScheduleDocument]
     * @throws AlreadyMemberException 이미 멤버인 유저입니다.
     */
    fun addMember(userId: ObjectId): IndividualScheduleDocument{
        if(this.individualScheduleMap.containsKey(userId))
            throw AlreadyMemberException()
        individualScheduleMap[userId] = IndividualScheduleList()
        return this
    }

    /**
     * 해당 개인 일정 목록에 멤버를 제거하는 메서드
     * @param userId 제거하려는 유저의 object id
     * @return [IndividualScheduleDocument]
     * @throws NotFoundMemberException 해당 사용자가 존재하지 않습니다.
     */
    fun removeMember(userId: ObjectId): IndividualScheduleDocument{
        if(individualScheduleMap.remove(userId) == null)
            throw NotFoundMemberException()
        return this
    }

    /**
     * 해당 개인 일정 목록에서 사용자의 일정을 찾는 메서드
     * @param userId 찾으려는 유저의 object id
     * @return [IndividualScheduleList]
     * @throws NotFoundMemberException 해당 사용자가 존재하지 않습니다.
     */
    fun findMemberById(userId: ObjectId): IndividualScheduleList {
        return individualScheduleMap[userId] ?: throw NotFoundMemberException()
    }
}
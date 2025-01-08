package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.group.AlreadyMemberException
import vp.togedo.model.exception.group.NotFoundGroupScheduleException
import vp.togedo.model.exception.group.NotFoundMemberException

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
    val groupSchedules: MutableList<GroupScheduleElement> = mutableListOf()
){
    /**
     * 그룹의 정보를 수정
     * @param name 변경할 이름
     * @return 변경된 그룹
     */
    fun updateGroup(name: String): GroupDocument{
        this.name = name
        return this
    }

    /**
     * 그룹에 멤버를 추가
     * @param userId 추가할 사용자의 object id
     * @return 변경된 그룹
     */
    fun addMember(userId: ObjectId): GroupDocument{
        if(userId in members)
            throw AlreadyMemberException()
        this.members.add(userId)
        return this
    }

    /**
     * 그룹에 멤버를 제거
     * @param userId 제거할 사용자의 object id
     * @return 변경된 그룹
     */
    fun removeMember(userId: ObjectId): GroupDocument{
        if(userId !in members)
            throw NotFoundMemberException()
        this.members.remove(userId)
        for (groupScheduleElement in groupSchedules){
            if(groupScheduleElement.state != GroupScheduleStateEnum.CONFIRMED){
                groupScheduleElement.scheduleMember.remove(userId)
                groupScheduleElement.confirmedUser.remove(userId)
            }
            else{
                groupScheduleElement.scheduleMember.remove(userId)
            }
        }
        return this
    }

    /**
     * 그룹에서 해당 그룹 일정의 인덱스를 탐색
     * @param groupScheduleId 탐색할 그룹 일정의 object id
     * @return 해당 그룹 일정의 인덱스
     * @throws NotFoundGroupScheduleException 해당 그룹 일정을 찾을 수 없음
     */
    fun findGroupScheduleIndexById(
        groupScheduleId: ObjectId
    ): Int{
        val index = groupSchedules.indexOfFirst { it.id == groupScheduleId }
        if (index == -1)
            throw NotFoundGroupScheduleException()
        return index
    }

}
package vp.togedo.model.documents.joinedGroup

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.joinedGroup.AlreadyJoinedGroupException
import vp.togedo.model.exception.joinedGroup.NotJoinedGroupException

@Document(collection = "joined_groups")
data class JoinedGroupDocument(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("groups")
    val groups: MutableList<ObjectId> = mutableListOf()
){
    /**
     * 새로운 그룹을 추가
     * @param groupId 추가할 그룹의 object id
     * @return [JoinedGroupDocument] 사용자의 JoinedGroupDocument
     * @throws AlreadyJoinedGroupException 이미 가입되어 있는 그룹입
     */
    fun addGroup(groupId: ObjectId): JoinedGroupDocument {
        if(groups.contains(groupId))
            throw AlreadyJoinedGroupException()
        groups.add(groupId)
        return this
    }

    /**
     * 그룹을 삭제
     * @param groupId 삭제할 그룹의 object id
     * @return [JoinedGroupDocument] 사용자의 JoinedGroupDocument
     * @throws NotJoinedGroupException 이미 가입되어 있는 그룹입
     */
    fun removeGroup(groupId: ObjectId): JoinedGroupDocument {
        if(groups.remove(groupId))
            return this
        else
            throw NotJoinedGroupException()
    }


}
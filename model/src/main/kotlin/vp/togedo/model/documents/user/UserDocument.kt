package vp.togedo.model.documents.user

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.user.*

@Document(collection = "users")
data class UserDocument(
    @Id
    val id: ObjectId = ObjectId.get(),

    @Indexed(unique = true)
    val oauth: Oauth,

    @Indexed(unique = true)
    var email: String? = null,

    var name: String,

    var profileImageUrl: String? = null,

    var friends: MutableSet<ObjectId> = mutableSetOf(),

    var friendRequests: MutableSet<ObjectId> = mutableSetOf(),

    var deviceToken: String? = null
){
    /**
     * 해당 유저에게 친구 요청
     * @param userId 친구 요청을 보냄
     * @return 친구 요청을 받은 사용자의 document
     * @throws CantRequestToMeException 해당 유저가 본인인 경우
     * @throws AlreadyFriendException 해당 친구가 이미 친구인 경우
     * @throws AlreadyRequestFriendException 해당 친구가 이미 친구 요청을 보낸 경우
     */
    fun addFriendRequest(userId: ObjectId): UserDocument {
        isNotMe(userId) && isNotFriend(userId) && isNotRequestFriend(userId)
        this.friendRequests.add(userId)
        return this
    }

    /**
     * 해당 유저를 친구에 저장
     * @param userId 친구로 저장할 유저의 object id
     * @return 친구가 저장된 사용자의 document
     * @throws CantRequestToMeException 해당 유저가 본인인 경우
     * @throws AlreadyFriendException 해당 유저가 이미 친구인 경우
     */
    fun addFriend(userId: ObjectId): UserDocument {
        isNotMe(userId) && isNotFriend(userId)
        this.friends.add(userId)
        return this
    }

    /**
     * 해당 유저의 친구 요청을 승인
     * @param userId 친구 요청을 승인할 유저의 object id
     * @return 친구가 저장된 사용자의 document
     */
    fun approveFriendRequest(userId: ObjectId): UserDocument {
        isNotMe(userId) && isNotFriend(userId) && isRequestFriend(userId)
        this.friendRequests.remove(userId)
        this.friends.add(userId)
        return this
    }

    /**
     * 해당 유저를 친구 목록에서 삭제
     * @param userId 친구 목록에서 삭제할 유저의 object id
     * @return 변경된 사용자의 document
     * @throws CantRequestToMeException 해당 유저가 본인인 경우
     */
    fun removeFriend(userId: ObjectId): UserDocument {
        isNotMe(userId) && isFriend(userId)
        this.friends.remove(userId)
        return this
    }

    /**
     * 해당 유저가 보낸 친구 요청을 삭제
     * @param userId 삭제할 유저의 object id
     * @return 변경된 사용자의 document
     * @return 변경된 사용자의 document
     * @throws FriendRequestNotSentException
     */
    fun removeFriendRequest(userId: ObjectId): UserDocument {
        isNotMe(userId) && isRequestFriend(userId)
        this.friendRequests.remove(userId)
        return this
    }

    /**
     * 해당 유저의 정보를 수정
     * @param name 변경할 사용자의 이름
     * @param email 변경할 사용자의 이메일
     * @return 변경된 사용자의 document
     */
    fun updateUserInfo(
        name: String,
        email: String?
    ): UserDocument {
        this.name = name
        this.email = email
        return this
    }

    /**
     * 해당 유저의 프로필 이미지를 수정
     * @param profileImageUrl 변경할 사용자 이미지의 주소
     * @return 변경된 사용자의 document
     */
    fun updateUserProfileImageUrl(
        profileImageUrl: String?
    ): UserDocument {
        this.profileImageUrl = profileImageUrl
        return this
    }

    /**
     * 해당 유저의 device token을 수정
     * @param deviceToken 변경할 사용자의 device token
     * @return 변경된 사용자의 document
     */
    fun updateUserDeviceToken(deviceToken: String?): UserDocument {
        this.deviceToken = deviceToken
        return this
    }

    /**
     * 해당 유저가 본인이 아닌지 확인
     * @param userId 확인할 유저의 object id
     * @return true
     * @throws CantRequestToMeException 해당 object id가 본인인 경우
     */
    private fun isNotMe(userId: ObjectId): Boolean{
        if(userId == this.id)
            throw CantRequestToMeException()
        return true
    }

    /**
     * 해당 유저가 친구가 아닌지 확인
     * @param userId 확인할 유저의 object id
     * @return true
     * @throws AlreadyFriendException 해당 object id가 이미 친구인 경우
     */
    private fun isNotFriend(userId: ObjectId): Boolean{
        if(friends.contains(userId))
            throw AlreadyFriendException()
        return true
    }

    /**
     * 해당 유저가 친구인지 확인
     * @param userId 확인할 유저의 object id
     * @return true
     * @throws NotFriendException 해당 유저가 친구가 아닌 경우
     */
    private fun isFriend(userId: ObjectId): Boolean{
        if(!friends.contains(userId))
            throw NotFriendException()
        return true
    }

    /**
     * 해당 유저가 친구 요청을 보낸지 않았는지 확인
     * @param userId 확인할 유저의 object id
     * @return true
     * @throws AlreadyRequestFriendException 해당 유저가 친구 요청을 보냈던 경우
     */
    private fun isNotRequestFriend(userId: ObjectId): Boolean{
        if(friendRequests.contains(userId))
            throw AlreadyRequestFriendException()
        return true
    }

    /**
     * 해당 유저가 친구 요청을 보낸지 확인
     * @param userId 확인할 유저의 object id
     * @return true
     * @throws FriendRequestNotSentException 해당 유저가 친구 요청을 보내지 않았을 경우
     */
    private fun isRequestFriend(userId: ObjectId): Boolean{
        if(!friendRequests.contains(userId))
            throw FriendRequestNotSentException()
        return true
    }
}

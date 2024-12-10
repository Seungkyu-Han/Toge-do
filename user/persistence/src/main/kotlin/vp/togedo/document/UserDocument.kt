package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
data class UserDocument(
    @Id
    var id: ObjectId?,

    @Indexed(unique = true)
    val oauth: Oauth,

    var name: String? = null,

    @Indexed(unique = true)
    var email: String? = null,

    var profileImageUrl: String? = null,

    var friends: MutableSet<ObjectId> = mutableSetOf(),

    var friendRequests: MutableSet<ObjectId> = mutableSetOf(),
){
    fun requestFriend(userId: ObjectId):UserDocument{
        if(friends.contains(userId))
            throw DataIntegrityViolationException("이미 친구인 사용자입니다.")
        this.friendRequests.add(userId)
        return this
    }
}

data class Oauth(
    var kakaoId: Long? = null,
    var googleId: String? = null
)
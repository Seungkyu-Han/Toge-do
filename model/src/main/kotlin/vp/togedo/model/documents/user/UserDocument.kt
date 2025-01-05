package vp.togedo.model.documents.user

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import vp.togedo.model.exception.user.AlreadyFriendException
import vp.togedo.model.exception.user.CantRequestToMeException

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
)
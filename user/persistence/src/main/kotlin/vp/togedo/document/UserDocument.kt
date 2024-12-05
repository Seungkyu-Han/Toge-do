package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import vp.togedo.enums.OauthEnum

@Document(collection = "user")
data class UserDocument(
    @Id
    var id: ObjectId?,

    @Indexed(unique = true)
    val oauth: Oauth,

    var name: String? = null,

    var email: String? = null,

    var profileImageUrl: String? = null,
)

data class Oauth(
    @Field(targetType = FieldType.STRING)
    val oauthType: OauthEnum,
    val kakaoId: Long? = null,
    val googleId: String? = null
)
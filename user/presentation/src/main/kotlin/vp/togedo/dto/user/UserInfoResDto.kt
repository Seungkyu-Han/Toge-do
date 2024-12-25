package vp.togedo.dto.user

import vp.togedo.document.UserDocument

data class UserInfoResDto(
    val id: String,
    val name: String?,
    val email: String?,
    val image: String?,
    val notification: Boolean
){
    constructor(userDocument: UserDocument) :
            this(
                id = userDocument.id!!.toString(),
                name = userDocument.name,
                email = userDocument.email,
                image = userDocument.profileImageUrl,
                notification = userDocument.deviceToken != null
            )
}
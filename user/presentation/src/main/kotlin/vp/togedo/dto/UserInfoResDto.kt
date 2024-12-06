package vp.togedo.dto

import vp.togedo.document.UserDocument

data class UserInfoResDto(
    val name: String?,
    val email: String?,
    val profileImageUrl: String?
){
    constructor(userDocument: UserDocument) :
            this(
                name = userDocument.name,
                email = userDocument.email,
                profileImageUrl = userDocument.profileImageUrl
            )
}
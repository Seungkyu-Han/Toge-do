package vp.togedo.util.error.exception

import vp.togedo.util.error.errorCode.ErrorCode

class FriendException(
    val errorCode: ErrorCode,
    val state: Int? = null
): RuntimeException(){
    override val message: String
        get() = this.errorCode.message
}
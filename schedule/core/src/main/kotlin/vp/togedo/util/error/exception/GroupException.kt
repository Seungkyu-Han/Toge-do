package vp.togedo.util.error.exception

import vp.togedo.util.error.errorCode.ErrorCode

class GroupException(
    val errorCode: ErrorCode,
): RuntimeException() {

    override val message: String
    get() = this.errorCode.message
}
package vp.togedo.connector


interface EmailConnector {

    suspend fun requestValidCode(email: String)

    suspend fun checkValidCode(code: String, email: String): Boolean
}
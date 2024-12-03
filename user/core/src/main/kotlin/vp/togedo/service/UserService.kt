package vp.togedo.service

import org.bson.types.ObjectId

interface UserService {

    fun createJwtAccessToken(id: ObjectId): String

    fun createJwtRefreshToken(id: ObjectId): String
}
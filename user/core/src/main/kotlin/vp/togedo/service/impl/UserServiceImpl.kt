package vp.togedo.service.impl

import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import vp.togedo.config.jwt.JwtTokenProvider
import vp.togedo.service.UserService

@Service
class UserServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
): UserService {

    override fun createJwtAccessToken(id: ObjectId): String {
        val userId = id.toHexString()
        return jwtTokenProvider.getAccessToken(userId)
    }

    override fun createJwtRefreshToken(id: ObjectId): String {
        val userId = id.toHexString()
        return jwtTokenProvider.getAccessToken(userId)
    }
}
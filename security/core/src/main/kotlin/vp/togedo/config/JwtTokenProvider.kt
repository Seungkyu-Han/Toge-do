package vp.togedo.config

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtTokenProvider(
    @Value("\${JWT.SECRET}")
    private val secret: String
) {

    fun getUserId(token: String): String? {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .body.get("id", String::class.java)
    }

    fun isAccessToken(token: String): Boolean{
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .header["type"].toString() == "access_token"
    }
}
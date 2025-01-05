package vp.togedo.security.util

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException

@Component
class HeaderUtil {

    fun extractAccessTokenFromHeader(authorizationHeader: String): String{
        if (authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.removePrefix("Bearer ")
        }else{
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED, "not valid header")
        }
    }

}
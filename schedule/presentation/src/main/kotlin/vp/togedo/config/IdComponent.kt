package vp.togedo.config

import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import vp.togedo.util.error.errorCode.ErrorCode
import vp.togedo.util.error.exception.UserException
import java.util.regex.PatternSyntaxException

@Component
class IdComponent {

    fun objectIdProvider(id: String): ObjectId {
        return try {
            ObjectId(id)
        }catch(illegalArgumentException: IllegalArgumentException){
            throw UserException(ErrorCode.INVALID_TOKEN)
        }catch(patternSyntaxException: PatternSyntaxException){
            throw UserException(ErrorCode.INVALID_TOKEN)
        }
    }
}
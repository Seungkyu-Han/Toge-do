package vp.togedo

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import vp.togedo.document.Oauth
import vp.togedo.document.UserDocument

@Repository
interface UserRepository: ReactiveMongoRepository<UserDocument, ObjectId>{

    fun findByOauth(oauth: Oauth): Mono<UserDocument>
}
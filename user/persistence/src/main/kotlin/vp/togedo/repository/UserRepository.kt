package vp.togedo.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import vp.togedo.model.documents.user.UserDocument

@Repository
interface UserRepository: ReactiveMongoRepository<UserDocument, ObjectId>{

    fun findByOauth_KakaoId(kakaoId: Long): Mono<UserDocument>
    fun findByOauth_GoogleId(grantType: String): Mono<UserDocument>
    fun findByEmail(email: String): Mono<UserDocument>
}
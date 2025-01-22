package vp.togedo.redis.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Mono
import vp.togedo.model.documents.joinedGroup.JoinedGroupDocument
import vp.togedo.redis.config.ObjectIdModule
import vp.togedo.redis.repository.JoinedGroupRedisRepository
import java.time.Duration

class JoinedGroupRedisRepositoryImpl(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
): JoinedGroupRedisRepository {

    init{
        this.objectMapper.registerModule(ObjectIdModule())
    }

    private val joinedGroupRedisDuration = Duration.ofMinutes(20)
    private val joinedGroupPrefix = "joinedGroup:document:"

    override fun findById(id: ObjectId): Mono<JoinedGroupDocument> =
        reactiveRedisTemplate.opsForValue()
            .get(joinedGroupPrefix + id)
            .map{
                objectMapper.readValue(it, JoinedGroupDocument::class.java)
            }


    override fun save(joinedGroupDocument: JoinedGroupDocument): Mono<Void> =
        reactiveRedisTemplate.opsForValue()
            .set(
                joinedGroupPrefix + joinedGroupDocument.id.toString(),
                objectMapper.writeValueAsString(joinedGroupDocument),
                joinedGroupRedisDuration)
            .then()

}
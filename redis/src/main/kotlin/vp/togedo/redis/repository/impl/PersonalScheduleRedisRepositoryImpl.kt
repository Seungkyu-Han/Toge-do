package vp.togedo.redis.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import vp.togedo.model.documents.personalSchedule.PersonalScheduleDocument
import vp.togedo.redis.config.ObjectIdModule
import vp.togedo.redis.repository.PersonalScheduleRedisRepository
import java.time.Duration

@Repository
class PersonalScheduleRedisRepositoryImpl(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
): PersonalScheduleRedisRepository {

    init{
        this.objectMapper.registerModule(ObjectIdModule())
    }

    private val personalScheduleRedisDuration = Duration.ofMinutes(20)
    private val personalSchedulePrefix = "personalSchedule:document:"

    override fun findById(id: ObjectId): Mono<PersonalScheduleDocument> =
        reactiveRedisTemplate.opsForValue()
            .get(personalSchedulePrefix + id)
            .map{
                objectMapper.readValue(it, PersonalScheduleDocument::class.java)
            }

    override fun save(personalScheduleDocument: PersonalScheduleDocument): Mono<Void> =
        reactiveRedisTemplate.opsForValue()
            .set(
                personalSchedulePrefix + personalScheduleDocument.id.toString(),
                objectMapper.writeValueAsString(personalScheduleDocument),
                personalScheduleRedisDuration)
            .then()
}
package vp.togedo.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.document.PersonalScheduleDocument
import vp.togedo.repository.PersonalScheduleRepository
import vp.togedo.repository.mongo.PersonalScheduleMongoRepository

@Repository
class PersonalScheduleRepositoryImpl(
    private val personalScheduleMongoRepository: PersonalScheduleMongoRepository,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
): PersonalScheduleRepository {

    private val redisPrefix = "personalSchedule:document:"

    override fun findByUserId(userId: ObjectId): Mono<PersonalScheduleDocument> {
        return reactiveRedisTemplate.opsForValue()
            .get("$redisPrefix$userId")
            .map{
                objectMapper.readValue(it, PersonalScheduleDocument::class.java)
            }
            .switchIfEmpty(
                personalScheduleMongoRepository.findByUserId(userId)
            )
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                if (it != null)
                    reactiveRedisTemplate.opsForValue()
                        .set(
                            "$redisPrefix$userId",
                            objectMapper.writeValueAsString(it),
                            ).block()
            }
    }

    override fun save(personalScheduleDocument: PersonalScheduleDocument): Mono<PersonalScheduleDocument> {
        return personalScheduleMongoRepository.save(personalScheduleDocument)
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                reactiveRedisTemplate.opsForValue()
                    .set(
                        "$redisPrefix${it.userId}",
                        objectMapper.writeValueAsString(it),
                    ).block()
            }
    }
}
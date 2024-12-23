package vp.togedo.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.bson.types.ObjectId
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.document.PersonalScheduleDocument
import vp.togedo.repository.PersonalScheduleRepository
import vp.togedo.repository.mongo.PersonalScheduleMongoRepository
import java.time.Duration

@Repository
class PersonalScheduleRepositoryImpl(
    private val personalScheduleMongoRepository: PersonalScheduleMongoRepository,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
): PersonalScheduleRepository {

    private val personalScheduleRedisTime = Duration.ofHours(2)

    init{
        class ObjectIdSerializer : com.fasterxml.jackson.databind.JsonSerializer<ObjectId>() {
            override fun serialize(value: ObjectId, gen: com.fasterxml.jackson.core.JsonGenerator, serializers: com.fasterxml.jackson.databind.SerializerProvider) {
                gen.writeString(value.toHexString())
            }
        }

        class ObjectIdDeserializer : com.fasterxml.jackson.databind.JsonDeserializer<ObjectId>() {
            override fun deserialize(p: com.fasterxml.jackson.core.JsonParser, ctxt: com.fasterxml.jackson.databind.DeserializationContext): ObjectId {
                return ObjectId(p.valueAsString)
            }
        }

        class ObjectIdModule : SimpleModule() {
            init {
                addSerializer(ObjectId::class.java, ObjectIdSerializer())
                addDeserializer(ObjectId::class.java, ObjectIdDeserializer())
            }
        }

        this.objectMapper.registerModule(ObjectIdModule())
    }

    private val redisPrefix = "personalSchedule:document:"

    override fun findByUserId(userId: ObjectId): Mono<PersonalScheduleDocument> {
        return reactiveRedisTemplate.opsForValue()
            .get("$redisPrefix$userId")
            .map{
                objectMapper.readValue(it, PersonalScheduleDocument::class.java)
            }
            .switchIfEmpty(
                Mono.defer{personalScheduleMongoRepository.findByUserId(userId) }
            )
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                if (it != null)
                    reactiveRedisTemplate.opsForValue()
                        .set(
                            "$redisPrefix$userId",
                            objectMapper.writeValueAsString(it),
                            personalScheduleRedisTime
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
                        personalScheduleRedisTime
                    ).block()
            }
    }
}
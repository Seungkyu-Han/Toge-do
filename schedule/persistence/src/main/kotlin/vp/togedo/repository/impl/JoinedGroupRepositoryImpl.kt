package vp.togedo.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.bson.types.ObjectId
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import vp.togedo.document.JoinedGroupDocument
import vp.togedo.repository.JoinedGroupRepository
import vp.togedo.repository.mongo.JoinedGroupMongoRepository
import java.time.Duration

@Repository
class JoinedGroupRepositoryImpl(
    private val joinedGroupMongoRepository: JoinedGroupMongoRepository,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
): JoinedGroupRepository {

    private val joinedGroupRedisTime = Duration.ofHours(2)
    private val redisPrefix = "joinedGroup:document:"

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

    override fun findById(id: ObjectId): Mono<JoinedGroupDocument> {
        return reactiveRedisTemplate.opsForValue()
            .get("$redisPrefix$id")
            .map{
                objectMapper.readValue(it, JoinedGroupDocument::class.java)
            }
            .switchIfEmpty(
                Mono.defer{joinedGroupMongoRepository.findById(id)}
            )
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                if (it != null)
                    reactiveRedisTemplate.opsForValue()
                        .set(
                            "$redisPrefix$id",
                            objectMapper.writeValueAsString(it),
                            joinedGroupRedisTime
                            ).block()
            }
    }

    override fun save(joinedGroupDocument: JoinedGroupDocument): Mono<JoinedGroupDocument> {
        return joinedGroupMongoRepository.save(joinedGroupDocument)
            .publishOn(Schedulers.boundedElastic())
            .doOnSuccess {
                reactiveRedisTemplate.opsForValue()
                    .set(
                        "$redisPrefix${it.id}",
                        objectMapper.writeValueAsString(it),
                        joinedGroupRedisTime
                    ).block()
            }
    }
}
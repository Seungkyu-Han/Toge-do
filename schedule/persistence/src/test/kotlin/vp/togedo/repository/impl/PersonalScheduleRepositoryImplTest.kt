package vp.togedo.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import vp.togedo.document.PersonalScheduleDocument
import vp.togedo.repository.mongo.PersonalScheduleMongoRepository
import java.time.Duration

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [PersonalScheduleRepositoryImpl::class, ObjectMapper::class])
class PersonalScheduleRepositoryImplTest{
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

    @MockBean
    private lateinit var personalScheduleMongoRepository: PersonalScheduleMongoRepository

    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    private val objectMapper: ObjectMapper = ObjectMapper()

    private lateinit var personalScheduleRepositoryImpl: PersonalScheduleRepositoryImpl

    private val personalScheduleRedisTime = Duration.ofHours(2)

    @BeforeEach
    fun setUp() {
        this.objectMapper.registerModule(ObjectIdModule())
        personalScheduleRepositoryImpl = PersonalScheduleRepositoryImpl(
            personalScheduleMongoRepository = personalScheduleMongoRepository,
            reactiveRedisTemplate = reactiveRedisTemplate,
            objectMapper = objectMapper
        )
    }

    private val redisPrefix = "personalSchedule:document:"

    @Nested
    inner class FindByUserId{

        @Test
        @DisplayName("Redis에 조회하려는 값이 있는 경우")
        fun findByUserIdAndRedisHaveResultReturnSuccess(){
            //given
            val userId = ObjectId.get()
            val personalSchedule = PersonalScheduleDocument(
                userId = userId,
            )
            val personalScheduleToString = objectMapper.writeValueAsString(personalSchedule)
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$userId"))
                .thenReturn(Mono.just(personalScheduleToString))

            `when`(reactiveValueOperations.set(
                "$redisPrefix$userId",
                personalScheduleToString,
                personalScheduleRedisTime
            )).thenReturn(Mono.just(true))

            //when
            StepVerifier.create(personalScheduleRepositoryImpl.findByUserId(userId))
                .expectNextMatches { it.userId == personalSchedule.userId }.verifyComplete()

            //then

            verify(personalScheduleMongoRepository, times(0)).findByUserId(userId)
            verify(reactiveValueOperations, times(1)).set(
                "$redisPrefix$userId",
                personalScheduleToString,
                personalScheduleRedisTime
            )
        }

        @Test
        @DisplayName("Redis에 조회하려는 값이 없고 MongoDB에 있는 경우")
        fun findByUserIdAndMongoDBHaveResultReturnSuccess(){
            //given
            val userId = ObjectId.get()
            val personalSchedule = PersonalScheduleDocument(
                userId = userId,
            )
            val personalScheduleToString = objectMapper.writeValueAsString(personalSchedule)
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$userId"))
                .thenReturn(Mono.empty())

            `when`(personalScheduleMongoRepository.findByUserId(userId))
                .thenReturn(Mono.just(personalSchedule))

            `when`(reactiveValueOperations.set(
                "$redisPrefix$userId",
                personalScheduleToString,
                personalScheduleRedisTime
            )).thenReturn(Mono.just(true))

            //when
            StepVerifier.create(personalScheduleRepositoryImpl.findByUserId(userId))
                .expectNextMatches { it.userId == personalSchedule.userId }.verifyComplete()

            //then

            verify(reactiveValueOperations, times(1)).get("$redisPrefix$userId")
            verify(personalScheduleMongoRepository, times(1)).findByUserId(userId)
            verify(reactiveValueOperations, times(1)).set(
                "$redisPrefix$userId",
                personalScheduleToString,
                personalScheduleRedisTime
            )
        }

        @Test
        @DisplayName("Redis와 MongoDB에 모두 데이터가 있는 경우")
        fun findByUserIdAndRedisMongoDBHaveResultReturnSuccess(){
            //given
            val userId = ObjectId.get()
            val personalSchedule = PersonalScheduleDocument(
                userId = userId,
            )
            val personalScheduleToString = objectMapper.writeValueAsString(personalSchedule)
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$userId"))
                .thenReturn(Mono.just(personalScheduleToString))

            `when`(personalScheduleMongoRepository.findByUserId(userId))
                .thenReturn(Mono.just(personalSchedule))

            `when`(reactiveValueOperations.set(
                "$redisPrefix$userId",
                personalScheduleToString,
                personalScheduleRedisTime
            )).thenReturn(Mono.just(true))

            //when
            StepVerifier.create(personalScheduleRepositoryImpl.findByUserId(userId))
                .expectNextMatches { it.userId == personalSchedule.userId }.verifyComplete()

            //then

            verify(reactiveValueOperations, times(1)).get("$redisPrefix$userId")
            verify(personalScheduleMongoRepository, times(0)).findByUserId(userId)
            verify(reactiveValueOperations, times(1)).set(
                "$redisPrefix$userId",
                personalScheduleToString,
                personalScheduleRedisTime
            )
        }

        @Test
        @DisplayName("Redis, MongoDB 모두 데이터가 없는 경우")
        fun findByUserIdAndNoHaveDataReturnSuccess(){
            //given
            val userId = ObjectId.get()
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$userId"))
                .thenReturn(Mono.empty())

            `when`(personalScheduleMongoRepository.findByUserId(userId))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(personalScheduleRepositoryImpl.findByUserId(userId))
                .verifyComplete()

            //then
            verify(reactiveValueOperations, times(1)).get("$redisPrefix$userId")
            verify(personalScheduleMongoRepository, times(1)).findByUserId(userId)
            verify(reactiveValueOperations, times(0)).set(
                eq("$redisPrefix$userId"),
                any(),
                eq(personalScheduleRedisTime)
            )
        }
    }


}
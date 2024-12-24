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
import vp.togedo.document.JoinedGroupDocument
import vp.togedo.repository.mongo.JoinedGroupMongoRepository
import java.time.Duration

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [JoinedGroupRepositoryImpl::class, ObjectMapper::class])
class JoinedGroupRepositoryImplTest{

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
    private lateinit var joinedGroupMongoRepository: JoinedGroupMongoRepository

    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    private val objectMapper: ObjectMapper = ObjectMapper()

    private lateinit var joinedGroupRepositoryImpl: JoinedGroupRepositoryImpl

    private val redisPrefix = "joinedGroup:document:"

    private val joinedGroupRedisTime = Duration.ofHours(2)

    @BeforeEach
    fun setUp() {
        this.objectMapper.registerModule(ObjectIdModule())
        joinedGroupRepositoryImpl = JoinedGroupRepositoryImpl(
            joinedGroupMongoRepository = joinedGroupMongoRepository,
            reactiveRedisTemplate = reactiveRedisTemplate,
            objectMapper = objectMapper
        )
    }

    @Nested
    inner class FindById{

        @Test
        @DisplayName("Redis에 조회하려는 값이 있는 경우")
        fun findByIdAndRedisHaveResultReturnSuccess(){
            //given
            val id = ObjectId.get()
            val joinedGroup = JoinedGroupDocument(
                id = id
            )
            val joinedGroupToString = objectMapper.writeValueAsString(joinedGroup)
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$id"))
                .thenReturn(Mono.just(joinedGroupToString))

            `when`(reactiveValueOperations.set(
                "$redisPrefix$id",
                joinedGroupToString,
                joinedGroupRedisTime
            )).thenReturn(Mono.just(true))

            //when
            StepVerifier.create(joinedGroupRepositoryImpl.findById(id))
                .expectNextMatches { it.id == joinedGroup.id }.verifyComplete()

            //then

            verify(joinedGroupMongoRepository, times(0)).findById(id)
            verify(reactiveValueOperations, times(1)).set(
                "$redisPrefix$id",
                joinedGroupToString,
                joinedGroupRedisTime
            )
        }

        @Test
        @DisplayName("Redis에 조회하려는 값이 없고 MongoDB에 있는 경우")
        fun findByIdAndMongoDBHaveResultReturnSuccess(){
            //given
            val id = ObjectId.get()
            val joinedGroup = JoinedGroupDocument(
                id = id
            )
            val joinedGroupToString = objectMapper.writeValueAsString(joinedGroup)
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$id"))
                .thenReturn(Mono.empty())

            `when`(joinedGroupMongoRepository.findById(id))
                .thenReturn(Mono.just(joinedGroup))

            `when`(reactiveValueOperations.set(
                "$redisPrefix$id",
                joinedGroupToString,
                joinedGroupRedisTime
            )).thenReturn(Mono.just(true))

            //when
            StepVerifier.create(joinedGroupRepositoryImpl.findById(id))
                .expectNextMatches { it.id == joinedGroup.id }.verifyComplete()

            //then

            verify(reactiveValueOperations, times(1)).get("$redisPrefix$id")
            verify(joinedGroupMongoRepository, times(1)).findById(id)
            verify(reactiveValueOperations, times(1)).set(
                "$redisPrefix$id",
                joinedGroupToString,
                joinedGroupRedisTime
            )
        }


        @Test
        @DisplayName("Redis와 MongoDB에 모두 데이터가 있는 경우")
        fun findByIdAndRedisMongoDBHaveResultReturnSuccess(){
            //given
            val id = ObjectId.get()
            val joinedGroup = JoinedGroupDocument(
                id = id
            )
            val joinedGroupToString = objectMapper.writeValueAsString(joinedGroup)
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$id"))
                .thenReturn(Mono.just(joinedGroupToString))

            `when`(joinedGroupMongoRepository.findById(id))
                .thenReturn(Mono.just(joinedGroup))

            `when`(reactiveValueOperations.set(
                "$redisPrefix$id",
                joinedGroupToString,
                joinedGroupRedisTime
            )).thenReturn(Mono.just(true))

            //when
            StepVerifier.create(joinedGroupRepositoryImpl.findById(id))
                .expectNextMatches { it.id == joinedGroup.id }.verifyComplete()

            //then

            verify(reactiveValueOperations, times(1)).get("$redisPrefix$id")
            verify(joinedGroupMongoRepository, times(0)).findById(id)
            verify(reactiveValueOperations, times(1)).set(
                "$redisPrefix$id",
                joinedGroupToString,
                joinedGroupRedisTime
            )
        }


        @Test
        @DisplayName("Redis, MongoDB 모두 데이터가 없는 경우")
        fun findByUserIdAndNoHaveDataReturnSuccess(){
            //given
            val id = ObjectId.get()
            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.get("$redisPrefix$id"))
                .thenReturn(Mono.empty())

            `when`(joinedGroupMongoRepository.findById(id))
                .thenReturn(Mono.empty())

            //when
            StepVerifier.create(joinedGroupRepositoryImpl.findById(id))
                .verifyComplete()

            //then
            verify(reactiveValueOperations, times(1)).get("$redisPrefix$id")
            verify(joinedGroupMongoRepository, times(1)).findById(id)
            verify(reactiveValueOperations, times(0)).set(
                eq("$redisPrefix$id"),
                any(),
                eq(joinedGroupRedisTime)
            )
        }
    }

    @Nested
    inner class Save{

        private var joinedGroup = JoinedGroupDocument(
            id = ObjectId.get()
        )

        @BeforeEach
        fun setUp() {
            joinedGroup = JoinedGroupDocument(
                id = ObjectId.get()
            )
        }

        @Test
        @DisplayName("데이터베이스와 Redis에 모두 저장")
        fun saveToBothMongoAndRedisReturnSuccess(){
            //given
            val joinedGroupToString = objectMapper.writeValueAsString(joinedGroup)

            `when`(joinedGroupMongoRepository.save(joinedGroup))
                .thenReturn(Mono.just(joinedGroup))

            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.set("$redisPrefix${joinedGroup.id}", joinedGroupToString, joinedGroupRedisTime))
                .thenReturn(Mono.just(true))

            //when
            StepVerifier.create(joinedGroupRepositoryImpl.save(joinedGroup))
                .expectNextMatches { it == joinedGroup }
                .verifyComplete()

            //then
            verify(joinedGroupMongoRepository, times(1)).save(joinedGroup)
            verify(reactiveValueOperations, times(1)).set(
                "$redisPrefix${joinedGroup.id}",
                joinedGroupToString,
                joinedGroupRedisTime
            )
        }

        @Test
        @DisplayName("데이터베이스 저장에 실패하면, Redis에 저장을 시도하지 않음")
        fun saveFailToBothMongoAndRedisReturnSuccess(){
            //given
            val joinedGroupToString = objectMapper.writeValueAsString(joinedGroup)

            `when`(joinedGroupMongoRepository.save(joinedGroup))
                .thenReturn(Mono.error(NullPointerException()))

            `when`(reactiveRedisTemplate.opsForValue())
                .thenReturn(reactiveValueOperations)

            `when`(reactiveValueOperations.set("$redisPrefix${joinedGroup.id}", joinedGroupToString, joinedGroupRedisTime))
                .thenReturn(Mono.just(true))

            //when
            StepVerifier.create(joinedGroupRepositoryImpl.save(joinedGroup))
                .expectErrorMatches {
                    it is NullPointerException
                }.verify()

            //then
            verify(joinedGroupMongoRepository, times(1)).save(joinedGroup)
            verify(reactiveValueOperations, times(0)).set(
                "$redisPrefix${joinedGroup.id}",
                joinedGroupToString,
                joinedGroupRedisTime
            )
        }
    }
}
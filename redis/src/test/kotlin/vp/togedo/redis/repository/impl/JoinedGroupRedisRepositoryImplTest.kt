package vp.togedo.redis.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
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
import vp.togedo.model.documents.joinedGroup.JoinedGroupDocument
import vp.togedo.redis.config.ObjectIdModule
import java.time.Duration

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [JoinedGroupRedisRepositoryImpl::class, ObjectMapper::class])
class JoinedGroupRedisRepositoryImplTest{

    private lateinit var joinedGroupRedisDuration: Duration
    private lateinit var joinedGroupPrefix: String

    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    private lateinit var joinedGroupRedisRepository: JoinedGroupRedisRepositoryImpl

    private val objectMapper: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        this.objectMapper.registerModule(ObjectIdModule())
        joinedGroupRedisRepository = JoinedGroupRedisRepositoryImpl(
            reactiveRedisTemplate = reactiveRedisTemplate,
            objectMapper = objectMapper,
        )
        `when`(reactiveRedisTemplate.opsForValue())
            .thenReturn(reactiveValueOperations)
        joinedGroupRedisDuration = joinedGroupRedisRepository.joinedGroupRedisDuration
        joinedGroupPrefix = joinedGroupRedisRepository.joinedGroupPrefix
    }

    @Nested
    inner class FindById{

        @Test
        @DisplayName("존재하는 joined group document 조회")
        fun findByIdToExistReturnSuccess(){
            //given
            val joinedGroupDocument = JoinedGroupDocument(
                id = ObjectId.get(),
                groups = mutableListOf(ObjectId.get())
            )

            `when`(reactiveValueOperations.get(
                joinedGroupPrefix + joinedGroupDocument.id,
            )).thenReturn(Mono.just(objectMapper.writeValueAsString(joinedGroupDocument)))

            //when
            StepVerifier.create(joinedGroupRedisRepository.findById(joinedGroupDocument.id))
                .expectNextMatches {
                    it == joinedGroupDocument
                }.verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(joinedGroupPrefix + joinedGroupDocument.id)
        }

        @Test
        @DisplayName("존재하지 않는 joined group document 조회")
        fun findByIdToNotExistReturnSuccess(){
            //given
            val joinedGroupId = ObjectId.get()

            `when`(reactiveValueOperations.get(
                joinedGroupPrefix + joinedGroupId,
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(joinedGroupRedisRepository.findById(joinedGroupId)).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(joinedGroupPrefix + joinedGroupId)
        }
    }

    @Nested
    inner class Save{
        @Test
        @DisplayName("joined group을 redis에 저장")
        fun saveValidCodeByEmailReturnSuccess(){
            //given
            val joinedGroupDocument = JoinedGroupDocument(
                id = ObjectId.get(),
                groups = mutableListOf(ObjectId.get())
            )

            val joinedGroupDocumentAsString = objectMapper.writeValueAsString(joinedGroupDocument)

            `when`(reactiveValueOperations.set(
                joinedGroupPrefix + joinedGroupDocument.id, joinedGroupDocumentAsString, joinedGroupRedisDuration
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(joinedGroupRedisRepository.save(
                joinedGroupDocument = joinedGroupDocument
            )).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .set(joinedGroupPrefix + joinedGroupDocument.id, joinedGroupDocumentAsString, joinedGroupRedisDuration)
        }
    }

}
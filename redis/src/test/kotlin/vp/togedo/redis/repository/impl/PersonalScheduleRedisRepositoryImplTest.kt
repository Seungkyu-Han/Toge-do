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
import vp.togedo.model.documents.personalSchedule.PersonalScheduleDocument
import vp.togedo.model.documents.personalSchedule.PersonalScheduleElement
import vp.togedo.redis.config.ObjectIdModule
import java.time.Duration
import java.util.UUID

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [PersonalScheduleRedisRepositoryImpl::class, ObjectMapper::class])
class PersonalScheduleRedisRepositoryImplTest{

    private lateinit var personalScheduleRedisDuration: Duration
    private lateinit var personalSchedulePrefix: String

    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    private lateinit var personalScheduleRedisRepository: PersonalScheduleRedisRepositoryImpl

    private val objectMapper: ObjectMapper = ObjectMapper()

    @BeforeEach
    fun setUp() {
        this.objectMapper.registerModule(ObjectIdModule())
        personalScheduleRedisRepository = PersonalScheduleRedisRepositoryImpl(
            reactiveRedisTemplate = reactiveRedisTemplate,
            objectMapper = objectMapper,
        )
        `when`(reactiveRedisTemplate.opsForValue())
            .thenReturn(reactiveValueOperations)
        personalSchedulePrefix = personalScheduleRedisRepository.personalSchedulePrefix
        personalScheduleRedisDuration = personalScheduleRedisRepository.personalScheduleRedisDuration
    }

    @Nested
    inner class FindById{

        @Test
        @DisplayName("존재하는 personal schedule document 조회")
        fun findByIdToExistReturnSuccess(){
            //given
            val fixedPersonalScheduleElement = PersonalScheduleElement(
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString(),
            )

            val flexiblePersonalScheduleElement = PersonalScheduleElement(
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString(),
            )

            val personalScheduleDocument = PersonalScheduleDocument(
                id = ObjectId.get(),
                fixedSchedules = mutableListOf(fixedPersonalScheduleElement),
                flexibleSchedules = mutableListOf(flexiblePersonalScheduleElement)
            )

            `when`(reactiveValueOperations.get(
                personalSchedulePrefix + personalScheduleDocument.id,
            )).thenReturn(Mono.just(objectMapper.writeValueAsString(personalScheduleDocument)))

            //when
            StepVerifier.create(personalScheduleRedisRepository.findById(personalScheduleDocument.id))
                .expectNextMatches {
                    it == personalScheduleDocument
                }.verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(personalSchedulePrefix + personalScheduleDocument.id)
        }

        @Test
        @DisplayName("존재하지 않는 joined group document 조회")
        fun findByIdToNotExistReturnSuccess(){
            //given
            val personalScheduleId = ObjectId.get()

            `when`(reactiveValueOperations.get(
                personalSchedulePrefix + personalScheduleId,
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(personalScheduleRedisRepository.findById(personalScheduleId)).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(personalSchedulePrefix + personalScheduleId)
        }
    }

    @Nested
    inner class Save{
        @Test
        @DisplayName("joined group을 redis에 저장")
        fun saveValidCodeByEmailReturnSuccess(){
            //given
            val fixedPersonalScheduleElement = PersonalScheduleElement(
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString(),
            )

            val flexiblePersonalScheduleElement = PersonalScheduleElement(
                startTime = UUID.randomUUID().toString(),
                endTime = UUID.randomUUID().toString(),
                name = UUID.randomUUID().toString(),
                color = UUID.randomUUID().toString(),
            )

            val personalScheduleDocument = PersonalScheduleDocument(
                id = ObjectId.get(),
                fixedSchedules = mutableListOf(fixedPersonalScheduleElement),
                flexibleSchedules = mutableListOf(flexiblePersonalScheduleElement)
            )

            val personalScheduleDocumentAsString = objectMapper.writeValueAsString(personalScheduleDocument)

            `when`(reactiveValueOperations.set(
                personalSchedulePrefix + personalScheduleDocument.id, personalScheduleDocumentAsString, personalScheduleRedisDuration
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(personalScheduleRedisRepository.save(
                personalScheduleDocument = personalScheduleDocument
            )).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .set(personalSchedulePrefix + personalScheduleDocument.id, personalScheduleDocumentAsString, personalScheduleRedisDuration)
        }
    }
}
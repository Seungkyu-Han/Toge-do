package vp.togedo.redis.service.impl

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
import java.time.Duration
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [ValidCodeServiceImpl::class])
class ValidCodeServiceImplTest{
    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    private lateinit var validCodeService: ValidCodeServiceImpl

    private lateinit var validCodePrefix: String

    @BeforeEach
    fun setUp() {
        validCodeService = ValidCodeServiceImpl(
            reactiveRedisTemplate = reactiveRedisTemplate
        )
        `when`(reactiveRedisTemplate.opsForValue())
            .thenReturn(reactiveValueOperations)

        validCodePrefix = validCodeService.validCodePrefix
    }

    @Nested
    inner class SaveCodeByEmail{

        @Test
        @DisplayName("이메일을 기준으로 인증번호를 저장")
        fun saveValidCodeByEmailReturnSuccess(){
            //given
            val email = UUID.randomUUID().toString()
            val validCode = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.set(
                validCodePrefix + email, validCode, Duration.ofMinutes(5)
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(validCodeService.saveCodeByEmail(
                email = email,
                code = validCode
            )).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .set(validCodePrefix + email, validCode, Duration.ofMinutes(5))
        }
    }

    @Nested
    inner class FindCodeByEmail{
        @Test
        @DisplayName("이메일을 기준으로 존재하는 인증번호를 조회")
        fun findCodeByEmailReturnSuccess(){
            //given
            val email = UUID.randomUUID().toString()
            val validCode = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.get(
                validCodePrefix + email
            )).thenReturn(Mono.just(validCode))

            //when
            StepVerifier.create(validCodeService.findCodeByEmail(email))
                .expectNextMatches {
                    it == validCode
                }.verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(validCodePrefix + email)
        }

        @Test
        @DisplayName("이메일을 기준으로 존재하지 않는 인증번호를 조회")
        fun findCodeByEmailReturnEmpty(){
            //given
            val email = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.get(
                validCodePrefix + email
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(validCodeService.findCodeByEmail(email)).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(validCodePrefix + email)
        }
    }

}
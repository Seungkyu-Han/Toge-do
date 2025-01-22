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
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DeviceTokenServiceImpl::class])
class DeviceTokenServiceImplTest{

    private val deviceTokenPrefix = "deviceToken:"

    @MockBean
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, String>

    @Mock
    private lateinit var reactiveValueOperations: ReactiveValueOperations<String, String>

    private lateinit var deviceTokenService: DeviceTokenServiceImpl

    @BeforeEach
    fun setUp() {
        deviceTokenService = DeviceTokenServiceImpl(
            reactiveRedisTemplate = reactiveRedisTemplate
        )
        `when`(reactiveRedisTemplate.opsForValue())
            .thenReturn(reactiveValueOperations)
    }

    @Nested
    inner class SaveDeviceToken {

        @Test
        @DisplayName("아이디를 기준으로 디바이스 토큰을 저장")
        fun saveDeviceTokenById(){
            //given
            val id = UUID.randomUUID().toString()
            val deviceToken = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.set(
                deviceTokenPrefix + id, deviceToken
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(deviceTokenService.saveDeviceToken(
                id = id,
                deviceToken = deviceToken
            )).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .set(deviceTokenPrefix + id, deviceToken)
        }
    }

    @Nested
    inner class FindById{

        @Test
        @DisplayName("아이디를 기준으로 존재하는 디바이스 토큰을 조회")
        fun findByIdReturnSuccess(){
            //given
            val id = UUID.randomUUID().toString()
            val deviceToken = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.get(
                deviceTokenPrefix + id
            )).thenReturn(Mono.just(deviceToken))

            //when
            StepVerifier.create(deviceTokenService.findById(id))
                .expectNextMatches {
                    it == deviceToken
                }.verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(deviceTokenPrefix + id)
        }

        @Test
        @DisplayName("아이디를 기준으로 존재하지 않는 디바이스 토큰을 조회")
        fun findByIdReturnEmpty(){
            //given
            val id = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.get(
                deviceTokenPrefix + id
            )).thenReturn(Mono.empty())

            //when
            StepVerifier.create(deviceTokenService.findById(id)).verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .get(deviceTokenPrefix + id)
        }
    }

    @Nested
    inner class DeleteById{

        @Test
        @DisplayName("존재하는 디바이스 토큰을 아이디를 기준으로 삭제")
        fun deleteByIdToExistReturnSuccess(){
            //given
            val id = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.delete(
                deviceTokenPrefix + id
            )).thenReturn(Mono.just(true))

            //when
            StepVerifier.create(deviceTokenService.deleteById(id))
                .verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .delete(deviceTokenPrefix + id)
        }

        @Test
        @DisplayName("존재하지 않는 디바이스 토큰을 아이디를 기준으로 삭제")
        fun deleteByIdToNotExistReturnSuccess(){
            //given
            val id = UUID.randomUUID().toString()

            `when`(reactiveValueOperations.delete(
                deviceTokenPrefix + id
            )).thenReturn(Mono.just(false))

            //when
            StepVerifier.create(deviceTokenService.deleteById(id))
                .verifyComplete()

            //then
            verify(reactiveValueOperations, times(1))
                .delete(deviceTokenPrefix + id)
        }
    }
}
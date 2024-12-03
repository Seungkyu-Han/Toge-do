package vp.togedo.service.impl

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import vp.togedo.UserRepository
import vp.togedo.config.jwt.JwtTokenProvider

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [UserServiceImpl::class])
class UserServiceImplTest{

    @SpyBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    private lateinit var userRepository: UserRepository

    private lateinit var userServiceImpl: UserServiceImpl

    @BeforeEach
    fun setUp() {
        userServiceImpl = UserServiceImpl(jwtTokenProvider, userRepository)
    }

}
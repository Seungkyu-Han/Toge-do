package vp.togedo.connector.impl

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import vp.togedo.connector.EmailConnector
import vp.togedo.kafka.data.email.ValidCodeEventDto
import vp.togedo.kafka.service.EmailKafkaService
import vp.togedo.redis.service.ValidCodeService
import vp.togedo.util.ValidationUtil

@Service
class EmailConnectorImpl(
    private val validCodeService: ValidCodeService,
    private val emailKafkaService: EmailKafkaService,
    private val validationUtil: ValidationUtil
): EmailConnector {

    override fun requestValidCode(email: String): Mono<Void> {

        val code = validationUtil.verificationCode()

        return validCodeService.saveCodeByEmail(
            email = email,
            code = code,
        ).then(
            emailKafkaService.publishSendValidCodeEvent(
                ValidCodeEventDto(
                    code = code,
                    email = email
                )
            )
        ).then()
    }

    override fun checkValidCode(code: String, email: String): Mono<Boolean> {
        return validCodeService.findCodeByEmail(email)
            .map{
                it == code
            }
    }
}
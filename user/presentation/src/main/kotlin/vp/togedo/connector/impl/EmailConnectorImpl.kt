package vp.togedo.connector.impl

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import vp.togedo.connector.EmailConnector
import vp.togedo.kafka.data.email.ValidCodeEventDto
import vp.togedo.kafka.service.EmailKafkaService
import vp.togedo.service.EmailService

@Service
class EmailConnectorImpl(
    private val emailService: EmailService,
    private val emailKafkaService: EmailKafkaService
): EmailConnector {

    override suspend fun requestValidCode(email: String){

        val code = emailService.createValidationCode(email).awaitSingle()

        emailKafkaService.publishSendValidCodeEvent(
            ValidCodeEventDto(
                code = code,
                email = email
            )
        ).awaitSingle()
    }

    override suspend fun checkValidCode(code: String, email: String): Boolean {
        return emailService.checkValidEmail(
            code = code, email = email
        ).awaitSingle()
    }
}
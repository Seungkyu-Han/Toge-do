package vp.togedo.connector.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import vp.togedo.connector.EmailConnector
import vp.togedo.service.EmailService

@Service
class EmailConnectorImpl(
    private val emailService: EmailService
): EmailConnector {

    override suspend fun requestValidCode(email: String){
        emailService.sendValidationCode(email).awaitSingleOrNull()
    }

    override suspend fun checkValidCode(code: String, email: String): Boolean {
        return emailService.checkValidEmail(
            code = code, email = email
        ).awaitSingle()
    }
}
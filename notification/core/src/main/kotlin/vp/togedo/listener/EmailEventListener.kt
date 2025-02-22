package vp.togedo.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import vp.togedo.kafka.config.Topics
import vp.togedo.kafka.data.email.ValidCodeEventDto
import vp.togedo.service.EmailService

@Component
class EmailEventListener(
    private val objectMapper: ObjectMapper,
    private val emailService: EmailService
) {

    @KafkaListener(topics = [Topics.SEND_EMAIL_VALIDATION_CODE], groupId = "seungkyu")
    fun sendValidationCode(message: String) {
        val validCodeEventDto = objectMapper.readValue(message, ValidCodeEventDto::class.java)
        emailService.sendEmail(
            address = validCodeEventDto.email,
            content = "인증번호입니다. ${validCodeEventDto.code}",
            title = "인증 메일입니다."
        )
    }
}
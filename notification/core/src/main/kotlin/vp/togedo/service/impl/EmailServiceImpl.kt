package vp.togedo.service.impl

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import vp.togedo.service.EmailService

@Service
class EmailServiceImpl(
    private val javaMailSender: JavaMailSender
): EmailService {

    override fun sendEmail(address: String, title: String, content: String) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "utf-8")

        helper.setSubject(title)
        helper.setText(content, true)
        helper.setTo(address)
        helper.setFrom("Toge-do")

        javaMailSender.send(mimeMessage)
    }
}
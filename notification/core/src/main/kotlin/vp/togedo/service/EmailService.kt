package vp.togedo.service

interface EmailService {

    fun sendEmail(
        address: String,
        title: String,
        content: String
    )
}
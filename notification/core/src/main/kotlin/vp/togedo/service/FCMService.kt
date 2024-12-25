package vp.togedo.service

interface FCMService {

    fun pushNotification(userId: String, title: String, content: String, image: String?)
}
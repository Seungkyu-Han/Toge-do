package vp.togedo.service

interface FCMService {

    fun pushNotification(deviceToken: String, title: String, content: String, image: String?)
}
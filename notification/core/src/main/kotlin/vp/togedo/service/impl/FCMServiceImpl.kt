package vp.togedo.service.impl

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import vp.togedo.service.FCMService
import java.io.FileInputStream

@Service
class FCMServiceImpl(
    @Value("\${FCM.CREDENTIALS}")
    private val credentials: String,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
): FCMService {

    private val deviceTokenPrefix = "deviceToken:"

    init{
        val serviceAccount = FileInputStream(credentials)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
    }

    override fun pushNotification(userId: String, title: String, content: String, image: String?) {
        mono {
            val notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build()

            val deviceToken = reactiveRedisTemplate.opsForValue().get("$deviceTokenPrefix$userId").awaitSingleOrNull()

            if(deviceToken != null) {
                println("This is deviceToken: $deviceToken")
                val message = Message.builder()
                    .setNotification(notification)
                    .setToken(deviceToken)
                    .putData("image", image ?: "")
                    .build()

                val result = FirebaseMessaging.getInstance().send(message)
                println("This is message: $message")
                println("result: $result")
            }
        }.subscribe()
    }
}
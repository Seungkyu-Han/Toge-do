package vp.togedo.service.impl

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import vp.togedo.service.FCMService
import java.io.FileInputStream

class FCMServiceImpl(
    @Value("\${FCM.CREDENTIALS}")
    private val credentials: String
): FCMService {

    init{
        val serviceAccount = FileInputStream(credentials)
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)
    }

    override fun pushNotification(deviceToken: String, title: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val notification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build()

            val message = Message.builder()
                .setNotification(notification)
                .setToken(deviceToken)
                .build()
            FirebaseMessaging.getInstance().send(message)
        }
    }
}
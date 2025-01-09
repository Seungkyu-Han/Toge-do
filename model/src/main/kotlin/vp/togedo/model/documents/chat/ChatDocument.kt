package vp.togedo.model.documents.chat

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chats")
data class ChatDocument(
    @Id
    val id: ObjectId = ObjectId.get(),

    @Indexed
    val groupId: ObjectId,

    val senderId: ObjectId,

    val message: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)

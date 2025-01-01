package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat")
data class ChatDocument(
    @Id
    val id: ObjectId = ObjectId.get(),

    @Indexed
    val groupId: ObjectId,

    val senderId: ObjectId,

    val message: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)

package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "fixed_personal_schedule")
data class FixedPersonalScheduleDocument(
    @Id
    var id: ObjectId? = null,

    @Indexed(unique = true)
    val userId: ObjectId,

    var schedules: MutableList<Schedule> = mutableListOf(),
)

data class Schedule(
    @Id
    var id: ObjectId = ObjectId.get(),

    var startTime: Int,

    var endTime: Int,

    var title: String,

    var color: String
)
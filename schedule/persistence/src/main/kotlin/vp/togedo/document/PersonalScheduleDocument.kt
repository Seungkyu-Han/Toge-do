package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "personal_schedule")
data class PersonalScheduleDocument(
    @Id
    val id: ObjectId = ObjectId.get(),

    @Indexed(unique = true)
    val userId: ObjectId,

    val fixedSchedules: MutableList<Schedule> = mutableListOf(),

    val flexibleSchedules: MutableList<Schedule> = mutableListOf()

)

data class Schedule(
    @Id
    val id: ObjectId = ObjectId.get(),
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val color: String,
    var friends: List<ObjectId>? = null
)
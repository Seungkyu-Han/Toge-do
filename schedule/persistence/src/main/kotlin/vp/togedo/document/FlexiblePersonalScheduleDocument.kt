package vp.togedo.document

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "flexible_personal_schedule")
data class FlexiblePersonalScheduleDocument(
    @Id
    var id: ObjectId = ObjectId.get(),

    @Indexed(unique = true)
    val userId: ObjectId,

    var flexibleSchedules: MutableList<FlexibleSchedule> = mutableListOf(),
){

}

data class FlexibleSchedule(
    @Id
    var id: ObjectId = ObjectId.get(),

    var startTime: Int,

    var endTime: Int,

    var title: String,

    var color: String,

    var friends: List<ObjectId> = mutableListOf()
)
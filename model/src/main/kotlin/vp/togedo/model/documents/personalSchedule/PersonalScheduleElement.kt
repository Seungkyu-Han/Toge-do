package vp.togedo.model.documents.personalSchedule

import org.bson.types.ObjectId

data class PersonalScheduleElement(
    val id: ObjectId = ObjectId.get(),
    val startTime: String,
    val endTime: String,
    val name: String,
    val color: String
)
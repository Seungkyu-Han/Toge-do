package vp.togedo.data.dao.personalSchedule

import org.bson.types.ObjectId

data class FixedScheduleDao(
    var scheduleId: ObjectId?,
    val startTime: String,
    val endTime: String,
    val name: String,
    val color: String
)

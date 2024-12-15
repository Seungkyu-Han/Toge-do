package vp.togedo.data.dao

import org.bson.types.ObjectId

data class ScheduleDao(
    var scheduleId: ObjectId?,
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val color: String
)

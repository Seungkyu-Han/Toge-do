package vp.togedo.data.dao.personalSchedule

import org.bson.types.ObjectId

data class FixedScheduleDao(
    var scheduleId: ObjectId?,
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val color: String
)

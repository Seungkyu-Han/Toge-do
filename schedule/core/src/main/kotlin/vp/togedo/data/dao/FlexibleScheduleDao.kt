package vp.togedo.data.dao

import org.bson.types.ObjectId

data class FlexibleScheduleDao(
    val scheduleId: ObjectId?,
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val color: String
)

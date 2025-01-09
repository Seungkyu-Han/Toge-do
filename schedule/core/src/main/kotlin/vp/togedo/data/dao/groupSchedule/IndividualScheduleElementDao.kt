package vp.togedo.data.dao.groupSchedule

import org.bson.types.ObjectId

data class IndividualScheduleElementDao(
    val id: ObjectId?,
    val startTime: String,
    val endTime: String,
)

package vp.togedo.data.dao.groupSchedule

import org.bson.types.ObjectId

data class GroupScheduleDao(
    val id: ObjectId?,
    val name: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val confirmScheduleDao: ConfirmScheduleDao?,
    val members: MutableSet<ObjectId>?
)
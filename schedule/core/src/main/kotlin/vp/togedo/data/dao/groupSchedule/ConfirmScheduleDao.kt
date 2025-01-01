package vp.togedo.data.dao.groupSchedule

import org.bson.types.ObjectId

data class ConfirmScheduleDao(
    val startTime: String?,
    val endTime: String?,
    val state: GroupScheduleStateDaoEnum,
    val confirmedUser: MutableSet<ObjectId>?
)

package vp.togedo.data.dao.groupSchedule

import org.bson.types.ObjectId

data class GroupScheduleDao(
    val id: ObjectId?,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val startTime: String,
    val endTime: String,
    val personalScheduleMap: Map<ObjectId, PersonalSchedulesDao>?,
    val confirmScheduleDao: ConfirmScheduleDao?
)

data class PersonalSchedulesDao(
    val personalSchedules: List<PersonalScheduleDao>
)

data class PersonalScheduleDao(
    val id: ObjectId?,
    val startTime: String,
    val endTime: String,
)
package vp.togedo.data.dao.Group

import org.bson.types.ObjectId

data class GroupScheduleDao(
    val id: ObjectId?,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val personalScheduleMap: Map<ObjectId, PersonalSchedulesDao>
)

data class PersonalSchedulesDao(
    val personalSchedules: List<PersonalScheduleDao>
)

data class PersonalScheduleDao(
    val startTime: Long,
    val endTime: Long,
)
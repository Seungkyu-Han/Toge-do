package vp.togedo.data.dao

import org.bson.types.ObjectId
import java.time.LocalDate

data class GroupScheduleDao(
    val id: ObjectId?,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val personalScheduleMap: Map<ObjectId, PersonalSchedulesDao>
)

data class PersonalSchedulesDao(
    val personalSchedules: List<PersonalScheduleDao>
)

data class PersonalScheduleDao(
    val startTime: Long,
    val endTime: Long,
)
package vp.togedo.data.dto.groupSchedule

data class GroupScheduleDetailDto(
    val id: String,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val startTime: String,
    val endTime: String,
    val personalScheduleMap: Map<String, PersonalSchedulesDto>,
    val confirmSchedule: ConfirmSchedule
)

data class PersonalSchedulesDto(
    val personalSchedules: List<PersonalScheduleDto>
)

data class PersonalScheduleDto(
    val id: String,
    val startTime: Long,
    val endTime: Long,
)

data class ConfirmSchedule(
    val state: String,
    val startTime: String?,
    val endTime: String?,
    val confirmedUser: List<String>?
)
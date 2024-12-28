package vp.togedo.data.dto.groupSchedule

data class GroupScheduleDetailDto(
    val id: String,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val personalScheduleMap: Map<String, PersonalSchedulesDto>
)

data class PersonalSchedulesDto(
    val personalSchedules: List<PersonalScheduleDto>
)

data class PersonalScheduleDto(
    val id: String,
    val startTime: Long,
    val endTime: Long,
)
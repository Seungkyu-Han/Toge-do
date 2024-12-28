package vp.togedo.data.dto.groupSchedule

data class GroupScheduleDto(
    val id: String,
    val name: String,
    val startDate: Long,
    val endDate: Long,
    val startTime: String,
    val endTime: String,
)

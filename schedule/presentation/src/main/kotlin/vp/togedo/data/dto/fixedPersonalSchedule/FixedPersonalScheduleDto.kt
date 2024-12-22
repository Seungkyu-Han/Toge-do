package vp.togedo.data.dto.fixedPersonalSchedule

data class FixedPersonalScheduleDto(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val color: String
)

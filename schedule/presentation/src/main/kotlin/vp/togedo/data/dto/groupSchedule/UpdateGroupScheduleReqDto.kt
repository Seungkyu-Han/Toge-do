package vp.togedo.data.dto.groupSchedule

data class UpdateGroupScheduleReqDto(
    val groupId: String,
    val scheduleId: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
)

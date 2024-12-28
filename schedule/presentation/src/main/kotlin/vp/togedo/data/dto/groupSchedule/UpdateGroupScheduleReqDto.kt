package vp.togedo.data.dto.groupSchedule

data class UpdateGroupScheduleReqDto(
    val groupId: String,
    val scheduleId: String,
    val name: String,
    val startDate: Long,
    val endDate: Long
)

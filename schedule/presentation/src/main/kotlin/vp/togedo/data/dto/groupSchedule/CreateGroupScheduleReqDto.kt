package vp.togedo.data.dto.groupSchedule

data class CreateGroupScheduleReqDto(
    val groupId: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String
)

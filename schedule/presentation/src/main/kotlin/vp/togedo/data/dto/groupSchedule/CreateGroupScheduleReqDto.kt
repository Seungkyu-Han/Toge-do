package vp.togedo.data.dto.groupSchedule

data class CreateGroupScheduleReqDto(
    val groupId: String,
    val name: String,
    val startDate: Long,
    val endDate: Long
)

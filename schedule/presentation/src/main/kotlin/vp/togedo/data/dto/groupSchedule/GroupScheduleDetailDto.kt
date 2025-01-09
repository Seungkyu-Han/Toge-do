package vp.togedo.data.dto.groupSchedule

data class GroupScheduleDetailDto(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val confirmSchedule: ConfirmSchedule
)

data class ConfirmSchedule(
    val state: String,
    val startTime: String?,
    val endTime: String?,
    val confirmedUser: List<String>?
)
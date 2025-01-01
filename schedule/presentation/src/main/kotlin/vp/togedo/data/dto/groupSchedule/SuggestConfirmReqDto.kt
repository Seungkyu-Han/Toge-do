package vp.togedo.data.dto.groupSchedule

data class SuggestConfirmReqDto(
    val groupId: String,
    val scheduleId: String,
    val startTime: String,
    val endTime: String
)

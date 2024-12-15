package vp.togedo.data.dto.fixedPersonalSchedule


data class UpdateFixedReqDto(
    val id: String,
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val color: String
)

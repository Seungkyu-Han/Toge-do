package vp.togedo.data.dto.fixedPersonalSchedule


data class UpdateFixedReqDto(
    val id: String,
    val startTime: String,
    val endTime: String,
    val name: String,
    val color: String
)

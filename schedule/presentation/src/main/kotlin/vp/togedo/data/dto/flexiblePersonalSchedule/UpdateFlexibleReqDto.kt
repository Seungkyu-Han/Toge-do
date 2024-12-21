package vp.togedo.data.dto.flexiblePersonalSchedule


data class UpdateFlexibleReqDto(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val color: String
)

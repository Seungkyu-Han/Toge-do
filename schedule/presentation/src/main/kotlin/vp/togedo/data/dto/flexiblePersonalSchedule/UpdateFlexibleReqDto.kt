package vp.togedo.data.dto.flexiblePersonalSchedule


data class UpdateFlexibleReqDto(
    val id: String,
    val startTime: String,
    val endTime: String,
    val name: String,
    val color: String
)

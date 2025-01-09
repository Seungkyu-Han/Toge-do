package vp.togedo.data.dto.flexiblePersonalSchedule

data class CreateFlexibleReqDto(
    val startTime: String,
    val endTime: String,
    val name: String,
    val color: String
)
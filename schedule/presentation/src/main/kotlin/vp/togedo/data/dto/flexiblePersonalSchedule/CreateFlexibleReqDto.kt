package vp.togedo.data.dto.flexiblePersonalSchedule

data class CreateFlexibleReqDto(
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val color: String
)
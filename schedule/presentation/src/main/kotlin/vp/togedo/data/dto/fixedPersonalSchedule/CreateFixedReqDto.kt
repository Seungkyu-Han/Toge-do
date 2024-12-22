package vp.togedo.data.dto.fixedPersonalSchedule

data class CreateFixedReqDto(
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val color: String
)

package vp.togedo.data.dto.fixedPersonalSchedule

data class CreateFixedReqDto(
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val color: String
)

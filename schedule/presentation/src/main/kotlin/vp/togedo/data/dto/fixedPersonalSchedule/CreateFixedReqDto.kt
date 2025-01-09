package vp.togedo.data.dto.fixedPersonalSchedule

data class CreateFixedReqDto(
    val startTime: String,
    val endTime: String,
    val name: String,
    val color: String
)

package vp.togedo.data.dto.flexiblePersonalSchedule


data class UpdateFlexibleReqDto(
    val id: String,
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val color: String,
    val friends: List<String>
)

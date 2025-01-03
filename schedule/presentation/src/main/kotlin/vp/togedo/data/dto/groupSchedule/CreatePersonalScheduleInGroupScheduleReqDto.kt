package vp.togedo.data.dto.groupSchedule

data class CreatePersonalScheduleInGroupScheduleReqDto(
    val groupId: String,
    val scheduleId: String,
    val personalSchedules: List<CreatePersonalScheduleDto>
)

data class CreatePersonalScheduleDto(
    val startTime: String,
    val endTime: String
)
package vp.togedo.data.dto.groupSchedule

data class UpdatePersonalScheduleInGroupScheduleReqDto(
    val groupId: String,
    val scheduleId: String,
    val personalSchedules: List<UpdatePersonalScheduleDto>
)

data class UpdatePersonalScheduleDto(
    val personalScheduleId: String,
    val startTime: Long,
    val endTime: Long
)
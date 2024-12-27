package vp.togedo.data.dto.groupSchedule

import java.time.LocalDate

data class CreateGroupScheduleReqDto(
    val groupId: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)

package vp.togedo.data.dto.groupSchedule

import java.time.LocalDate

data class GroupScheduleDetailDto(
    val id: String,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val personalScheduleMap: Map<String, PersonalSchedulesDto>
)

data class PersonalSchedulesDto(
    val personalSchedules: List<PersonalScheduleDto>
)

data class PersonalScheduleDto(
    val startTime: Long,
    val endTime: Long,
)
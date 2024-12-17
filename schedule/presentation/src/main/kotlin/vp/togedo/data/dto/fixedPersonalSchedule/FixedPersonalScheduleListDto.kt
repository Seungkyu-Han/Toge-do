package vp.togedo.data.dto.fixedPersonalSchedule

import com.fasterxml.jackson.annotation.JsonProperty

data class FixedPersonalScheduleListDto(
    @JsonProperty("fixedSchedules")
    val schedules: List<FixedPersonalScheduleDto>
)
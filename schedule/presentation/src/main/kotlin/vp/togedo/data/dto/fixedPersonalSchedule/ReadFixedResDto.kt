package vp.togedo.data.dto.fixedPersonalSchedule

import com.fasterxml.jackson.annotation.JsonProperty

data class ReadFixedResDto(
    @JsonProperty("schedules")
    val schedules: List<FixedPersonalScheduleDto>
)
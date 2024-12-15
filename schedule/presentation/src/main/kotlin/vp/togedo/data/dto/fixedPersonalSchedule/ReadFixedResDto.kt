package vp.togedo.data.dto.fixedPersonalSchedule

import com.fasterxml.jackson.annotation.JsonProperty

data class ReadFixedResDto(
    @JsonProperty("schedules")
    val schedules: List<FixedPersonalScheduleElement>
)

data class FixedPersonalScheduleElement(
    val id: String,
    val startTime: Int,
    val endTime: Int,
    val title: String,
    val color: String
)

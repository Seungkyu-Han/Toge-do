package vp.togedo.model.documents.group

import com.fasterxml.jackson.annotation.JsonProperty

data class IndividualScheduleList(
    @JsonProperty("individualSchedules")
    val individualSchedules: MutableList<IndividualSchedule> = mutableListOf(),
)
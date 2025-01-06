package vp.togedo.model.documents.personalSchedule

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "personal_schedules")
data class PersonalSchedule(
    @Id
    @JsonProperty("id")
    val id: ObjectId,

    @JsonProperty("fixedSchedules")
    val fixedSchedules: List<PersonalScheduleElement>,

    @JsonProperty("flexibleSchedules")
    val flexibleSchedules: List<PersonalScheduleElement>
){

}

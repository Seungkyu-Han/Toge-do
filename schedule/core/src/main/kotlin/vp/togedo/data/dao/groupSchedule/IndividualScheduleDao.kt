package vp.togedo.data.dao.groupSchedule

import org.bson.types.ObjectId

data class IndividualScheduleDao(
    val individualScheduleDaoMap: MutableMap<ObjectId, IndividualScheduleListDao> = mutableMapOf(),
)

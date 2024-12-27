package vp.togedo.data.dao.GroupSchedule

import org.bson.types.ObjectId

data class GroupDao(
    val id: ObjectId,
    val name: String,
    val members: List<ObjectId>
)

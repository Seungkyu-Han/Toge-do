package vp.togedo.data.dao.group

import org.bson.types.ObjectId

data class GroupDao(
    val id: ObjectId,
    val name: String,
    val members: List<ObjectId>
)

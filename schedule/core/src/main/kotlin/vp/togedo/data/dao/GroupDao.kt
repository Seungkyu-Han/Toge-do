package vp.togedo.data.dao

import org.bson.types.ObjectId

data class GroupDao(
    val id: ObjectId?,
    val name: String,
    val members: List<ObjectId>?
)

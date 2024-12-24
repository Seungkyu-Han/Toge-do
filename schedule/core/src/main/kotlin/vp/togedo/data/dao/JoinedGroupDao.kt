package vp.togedo.data.dao

import org.bson.types.ObjectId

data class JoinedGroupDao(
    val id: ObjectId,
    val groups: MutableSet<ObjectId>
)

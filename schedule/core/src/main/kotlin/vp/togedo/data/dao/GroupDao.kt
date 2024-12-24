package vp.togedo.data.dao

import org.bson.types.ObjectId

data class GroupDao(
    val name: String,
    val members: HashSet<ObjectId>?
)

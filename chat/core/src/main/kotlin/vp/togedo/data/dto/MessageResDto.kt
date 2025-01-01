package vp.togedo.data.dto

import java.time.LocalDateTime

data class MessageResDto(
    val senderId: String,
    val message: String,
    val createdAt: LocalDateTime
)

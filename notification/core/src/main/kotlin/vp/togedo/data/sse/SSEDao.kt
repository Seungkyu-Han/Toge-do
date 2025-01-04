package vp.togedo.data.sse

import vp.togedo.kafka.data.enums.EventEnums

data class SSEDao (
    val event: EventEnums,
    val sender: String,
    val image: String?
)

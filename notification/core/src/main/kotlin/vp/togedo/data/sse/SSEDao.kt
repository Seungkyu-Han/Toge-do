package vp.togedo.data.sse

data class SSEDao (
    val event: EventEnums,
    val sender: String,
    val image: String?
)

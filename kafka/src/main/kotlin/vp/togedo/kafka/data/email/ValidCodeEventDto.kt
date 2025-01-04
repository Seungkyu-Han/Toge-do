package vp.togedo.kafka.data.email

data class ValidCodeEventDto(
    val code: String,
    val email: String
)


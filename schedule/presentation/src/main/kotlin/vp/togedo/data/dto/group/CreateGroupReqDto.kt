package vp.togedo.data.dto.group

data class CreateGroupReqDto(
    val name: String,
    val members: List<String>
)

package vp.togedo.enums

enum class GroupScheduleStateEnum(
    val value: Int
) {

    DISCUSSING(0), REQUESTED(1), CONFIRMED(2);

    companion object {
        fun find(value: Int): GroupScheduleStateEnum? = entries.find { it.value == value }
    }

}
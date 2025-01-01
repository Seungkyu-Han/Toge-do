package vp.togedo.data.dao.groupSchedule

enum class GroupScheduleStateDaoEnum(
    val value: Int
) {

    DISCUSSING(0), REQUESTED(1), CONFIRMED(2);

    companion object {
        fun find(value: Int): GroupScheduleStateDaoEnum? = GroupScheduleStateDaoEnum.entries.find { it.value == value }
    }
}
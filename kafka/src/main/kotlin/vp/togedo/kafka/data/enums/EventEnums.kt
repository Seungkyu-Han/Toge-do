package vp.togedo.kafka.data.enums

import vp.togedo.kafka.config.Topics

enum class EventEnums(
    val eventValue: Int,
    val eventTitle: String,
    val eventContent: String,
    val topics: String
) {
    //FRIEND
    REQUEST_FRIEND_EVENT(0, "친구 요청이 왔습니다.", "님에게 친구 요청이 왔습니다.", Topics.FRIEND_REQUEST),
    APPROVE_FRIEND_EVENT(1, "상대방이 친구 요청을 수락했습니다.", "님이 친구 요청을 수락하였습니다.", Topics.FRIEND_APPROVE),

    //GROUP
    INVITE_GROUP(2, "그룹에 초대되었습니다.", "의 그룹에 초대되었습니다.", Topics.INVITE_GROUP),

    //GROUP SCHEDULE
    CREATE_GROUP_SCHEDULE(3, "공유 일정이 생성되었습니다.", "공유 일정이 생성되었습니다.", Topics.CREATE_GROUP_SCHEDULE),
    SUGGEST_CONFIRM_SCHEDULE(4, "일정 확인 요청입니다.", "일정 확인 요청입니다.", Topics.SUGGEST_CONFIRM_SCHEDULE),
    CONFIRM_SCHEDULE(5, "일정이 확정되었습니다.", "일정이 확정되었습니다.", Topics.CONFIRM_SCHEDULE),

}
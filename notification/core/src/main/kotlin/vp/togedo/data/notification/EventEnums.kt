package vp.togedo.data.notification

enum class EventEnums(
    val eventValue: Int,
    val eventTitle: String,
    val eventContent: String,
) {
    //FRIEND
    REQUEST_FRIEND_EVENT(0, "친구 요청이 왔습니다.", "님에게 친구 요청이 왔습니다."),
    APPROVE_FRIEND_EVENT(1, "상대방이 친구 요청을 수락했습니다.", "님이 친구 요청을 수락하였습니다."),

    //GROUP
    INVITE_GROUP(2, "그룹에 초대되었습니다.", "의 그룹에 초대되었습니다."),

    //GROUP SCHEDULE
    CREATE_GROUP_SCHEDULE(3, "공유 일정이 생성되었습니다.", "공유 일정이 생성되었습니다."),
    SUGGEST_CONFIRM_SCHEDULE(4, "일정 확인 요청입니다.", "일정 확인 요청입니다."),


}
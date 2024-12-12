package vp.togedo.data.notification

enum class EventEnums(
    val eventValue: Int,
    val eventTitle: String,
    val eventContent: String,
) {
    //FRIEND
    REQUEST_FRIEND_EVENT(0, "친구 요청이 왔습니다.", "님에게 친구 요청이 왔습니다."),
    APPROVE_FRIEND_EVENT(1, "상대방이 친구 요청을 수락했습니다.", "님이 친구 요청을 수락하였습니다."),

}
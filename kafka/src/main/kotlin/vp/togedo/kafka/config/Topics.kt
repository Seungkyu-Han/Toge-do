package vp.togedo.kafka.config

class Topics {

    companion object {
        //이메일 관련 토픽
        const val SEND_EMAIL_VALIDATION_CODE = "SEND_EMAIL_VALIDATION_CODE_TOPIC"

        //친구 관련 토픽
        const val FRIEND_REQUEST = "FRIEND_REQUEST_TOPIC"
        const val FRIEND_APPROVE = "FRIEND_APPROVE_TOPIC"

        //그룹 관련 토픽
        const val INVITE_GROUP = "INVITE_GROUP_TOPIC"

        //그룹 일정 관련 토픽
        const val CREATE_GROUP_SCHEDULE = "CREATE_GROUP_SCHEDULE_TOPIC"
        const val SUGGEST_CONFIRM_SCHEDULE = "SUGGEST_CONFIRM_SCHEDULE_TOPIC"
        const val CONFIRM_SCHEDULE = "CONFIRM_SCHEDULE_TOPIC"
    }
}
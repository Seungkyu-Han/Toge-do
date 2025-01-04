package vp.togedo.kafka.publish

import reactor.core.publisher.Mono
import vp.togedo.kafka.data.enums.EventEnums

interface KafkaPublisher {

    /**
     * 해당 토픽으로 kafka 이벤트를 전송하는 메서드
     * @param eventEnums 해당 이벤트의 정보
     * @param eventClass 메시지에 넣을 class
     * @return mono void
     */
    fun publishKafkaEvent(eventEnums: EventEnums, eventClass: Any): Mono<Void>
}
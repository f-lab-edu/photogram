package com.setung.producer

import com.setung.kafka.event.Event
import com.setung.kafka.event.EventType
import com.setung.kafka.event.payload.UserDeletedEventPayload
import com.setung.kafka.event.payload.UserFollowedEventPayload
import com.setung.kafka.event.payload.UserUnfollowedEventPayload
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    fun sendUserFollowEvent(requesterId: Long, targetId: Long) {
        kafkaTemplate.send(
            EventType.USER_FOLLOWED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.USER_FOLLOWED,
                payload = UserFollowedEventPayload(requesterId, targetId)
            ).toJson()
        )
    }

    fun sendUserUnfollowEvent(requesterId: Long, targetId: Long) {
        kafkaTemplate.send(
            EventType.USER_UNFOLLOWED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.USER_UNFOLLOWED,
                payload = UserUnfollowedEventPayload(requesterId, targetId)
            ).toJson()
        )
    }

    fun sendUserDeleteEvent(userId: Long, deletedUserFollowers: List<Long>) {
        kafkaTemplate.send(
            EventType.USER_DELETED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.USER_DELETED,
                payload = UserDeletedEventPayload(userId, deletedUserFollowers)
            ).toJson()
        )
    }
}
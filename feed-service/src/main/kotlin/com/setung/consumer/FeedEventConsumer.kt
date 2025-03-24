package com.setung.consumer

import com.setung.consumer.eventhandler.*
import com.setung.kafka.event.Event
import com.setung.kafka.event.EventPayload
import com.setung.kafka.event.EventTopics
import com.setung.kafka.event.EventType
import com.setung.kafka.event.payload.*
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class FeedEventConsumer(
    private val postUploadedEventHandler: PostUploadedEventHandler,
    private val postDeletedEventHandler: PostDeletedEventHandler,
    private val userFollowedEventHandler: UserFollowedEventHandler,
    private val userUnfollowedEventHandler: UserUnfollowedEventHandler,
    private val userDeletedEventHandler: UserDeletedEventHandler
) {

    @KafkaListener(
        topics = [
            EventTopics.POST_UPLOADED,
            EventTopics.POST_DELETED,
            EventTopics.USER_FOLLOWED,
            EventTopics.USER_UNFOLLOWED,
            EventTopics.USER_DELETED,
        ]
    )
    fun listen(message: String, ack: Acknowledgment) {
        val event: Event<EventPayload> = Event.fromJson(message)

        when (event.type) {
            EventType.POST_UPLOADED -> postUploadedEventHandler.handle(event as Event<PostUploadedEventPayload>)
            EventType.POST_DELETED -> postDeletedEventHandler.handle(event as Event<PostDeletedEventPayload>)
            EventType.USER_FOLLOWED -> userFollowedEventHandler.handle(event as Event<UserFollowedEventPayload>)
            EventType.USER_UNFOLLOWED -> userUnfollowedEventHandler.handle(event as Event<UserUnfollowedEventPayload>)
            EventType.USER_DELETED -> userDeletedEventHandler.handle(event as Event<UserDeletedEventPayload>)
        }

        ack.acknowledge()
    }

}
package com.setung.producer

import com.setung.client.UserClient
import com.setung.entity.PostEntity
import com.setung.kafka.event.Event
import com.setung.kafka.event.EventType
import com.setung.kafka.event.payload.PostDeletedEventPayload
import com.setung.kafka.event.payload.PostUploadedEventPayload
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class PostEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val userClient: UserClient,
) {

    fun sendPostUploadEvent(post: PostEntity) {
        val followerIds = userClient.getUserFollowers(post.writerId)

        kafkaTemplate.send(
            EventType.POST_UPLOADED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.POST_UPLOADED,
                payload = PostUploadedEventPayload(
                    postId = post.id!!,
                    followerIds = followerIds
                )
            ).toJson()
        )
    }

    fun sendPostDeleteEvent(post: PostEntity) {
        val followerIds = userClient.getUserFollowers(post.writerId)

        kafkaTemplate.send(
            EventType.POST_DELETED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.POST_DELETED,
                payload = PostDeletedEventPayload(
                    postId = post.id!!,
                    followerIds = followerIds
                )
            ).toJson()
        )
    }
}
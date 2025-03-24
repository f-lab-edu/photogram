package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.PostUploadedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class PostUploadedEventHandler(
    private val feedRepository: FeedRepository
) : EventHandler<PostUploadedEventPayload> {

    override fun handle(event: Event<PostUploadedEventPayload>) {
        val payload = event.payload
        feedRepository.addFeed(payload.postId, payload.followerIds)
    }

}
package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.PostDeletedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class PostDeletedEventHandler(
    private val feedRepository: FeedRepository
) : EventHandler<PostDeletedEventPayload> {

    override fun handle(event: Event<PostDeletedEventPayload>) {
        val payload = event.payload
        feedRepository.removeFeed(payload.followerIds, listOf(payload.postId))
    }

}
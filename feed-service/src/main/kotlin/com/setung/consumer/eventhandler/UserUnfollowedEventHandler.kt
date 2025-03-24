package com.setung.consumer.eventhandler

import com.setung.client.PostServiceClient
import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.UserUnfollowedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class UserUnfollowedEventHandler(
    private val feedRepository: FeedRepository,
    private val postServiceClient: PostServiceClient,
) : EventHandler<UserUnfollowedEventPayload> {

    override fun handle(event: Event<UserUnfollowedEventPayload>) {
        val payload = event.payload
        val postIds = postServiceClient.findAllIdsByWriterId(
            payload.targetId, 100
        )

        feedRepository.removeFeed(payload.requesterId, postIds)
    }

}
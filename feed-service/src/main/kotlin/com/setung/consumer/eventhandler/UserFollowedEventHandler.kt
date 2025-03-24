package com.setung.consumer.eventhandler

import com.setung.client.PostServiceClient
import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.UserFollowedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class UserFollowedEventHandler(
    private val feedRepository: FeedRepository,
    private val postServiceClient: PostServiceClient,
) : EventHandler<UserFollowedEventPayload> {

    override fun handle(event: Event<UserFollowedEventPayload>) {
        val payload = event.payload

        val postIds = postServiceClient.findAllIdsByWriterId(
            payload.targetId, 100
        )

        feedRepository.addFeed(postIds, payload.requesterId)
    }

}
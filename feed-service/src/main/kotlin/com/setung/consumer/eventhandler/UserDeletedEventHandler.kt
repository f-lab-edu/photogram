package com.setung.consumer.eventhandler

import com.setung.client.PostServiceClient
import com.setung.kafka.event.Event
import com.setung.kafka.event.payload.UserDeletedEventPayload
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Component

@Component
class UserDeletedEventHandler(
    private val feedRepository: FeedRepository,
    private val postServiceClient: PostServiceClient,
) : EventHandler<UserDeletedEventPayload> {

    override fun handle(event: Event<UserDeletedEventPayload>) {
        val payload = event.payload

        val postIds = postServiceClient.findAllIdsByWriterId(
            payload.deletedUserId, 100
        )

        feedRepository.removeFeed(payload.deletedUserFollowers, postIds)
    }

}
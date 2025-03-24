package com.setung.kafka.event

import com.setung.kafka.event.payload.*

enum class EventType(
    val topic: String,
    val payloadClass: Class<out EventPayload>
) {
    POST_UPLOADED(EventTopics.POST_UPLOADED, PostUploadedEventPayload::class.java),
    POST_DELETED(EventTopics.POST_DELETED, PostDeletedEventPayload::class.java),
    USER_FOLLOWED(EventTopics.USER_FOLLOWED, UserFollowedEventPayload::class.java),
    USER_UNFOLLOWED(EventTopics.USER_UNFOLLOWED, UserUnfollowedEventPayload::class.java),
    USER_DELETED(EventTopics.USER_DELETED, UserDeletedEventPayload::class.java);
}

object EventTopics {
    const val POST_UPLOADED = "post-upload"
    const val POST_DELETED = "post-delete"
    const val USER_FOLLOWED = "user-follow"
    const val USER_UNFOLLOWED = "user-unfollow"
    const val USER_DELETED = "user-delete"
}
package com.setung.kafka.event.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.setung.kafka.event.EventPayload

data class UserFollowedEventPayload(
    @JsonProperty("requesterId") val requesterId: Long,
    @JsonProperty("targetId") val targetId: Long
) : EventPayload {
}
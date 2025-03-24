package com.setung.kafka.event

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper

class Event<T : EventPayload>(
    val eventId: String,
    val type: EventType,
    val payload: T
) {

    companion object {
        private val objectMapper = ObjectMapper()

        fun of(eventId: String, type: EventType, payload: EventPayload) = Event(eventId, type, payload)

        fun of(eventRaw: EventRaw): Event<EventPayload> {
            val eventId = eventRaw.eventId
            val type = EventType.valueOf(eventRaw.type)
            val payloadJson = objectMapper.writeValueAsString(eventRaw.payload)
            val payload = objectMapper.readValue(payloadJson, type.payloadClass)
            return of(eventId, type, payload)
        }

        fun fromJson(json: String): Event<EventPayload> {
            val raw: EventRaw = objectMapper.readValue(json, EventRaw::class.java)
            return of(raw)
        }
    }

    fun toJson(): String {
        return objectMapper.writeValueAsString(this)
    }

    data class EventRaw(
        @JsonProperty("eventId") val eventId: String,
        @JsonProperty("type") val type: String,
        @JsonProperty("payload") val payload: Any
    )
}
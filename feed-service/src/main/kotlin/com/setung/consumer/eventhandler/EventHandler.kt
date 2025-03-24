package com.setung.consumer.eventhandler

import com.setung.kafka.event.Event
import com.setung.kafka.event.EventPayload


interface EventHandler<T : EventPayload> {

    fun handle(event: Event<T>)
}
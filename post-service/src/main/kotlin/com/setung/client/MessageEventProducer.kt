package com.setung.client

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class MessageEventProducer(

    @Value("\${spring.kafka.bootstrap-servers}")
    val bootstrapServers: String
) {

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        val config = mutableMapOf<String, String>()
        config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        config[ProducerConfig.ACKS_CONFIG] = "all"

        return KafkaTemplate(DefaultKafkaProducerFactory(config as Map<String, Any>))
    }
}
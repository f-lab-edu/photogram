package com.setung.config

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ExtendWith(SpringExtension::class)
@Testcontainers
@TestConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestContainerConfig {

    companion object {

        @Container
        private val redisContainer = GenericContainer("redis:latest").apply {
            withExposedPorts(6379)
            withReuse(true)
        }.also {
            it.start()
            System.setProperty("spring.data.redis.host", it.host)
            System.setProperty("spring.data.redis.port", it.getMappedPort(6379).toString())
        }

    }
}
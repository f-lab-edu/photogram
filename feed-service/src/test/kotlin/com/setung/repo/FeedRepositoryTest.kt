package com.setung.repo

import com.setung.config.TestContainerConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate

@SpringBootTest
@Import(TestContainerConfig::class)
class FeedRepositoryTest {

    @Autowired
    lateinit var feedRepository: FeedRepository

    @Autowired
    lateinit var redisTemplate: StringRedisTemplate

    @BeforeEach
    fun beforeEach() {
        redisTemplate.execute { it.serverCommands().flushAll() }
    }

    @Test
    @DisplayName("feed 푸시 및 조회 테스트")
    fun feedPushAndGetTest() {
        feedRepository.addFeed(1, listOf(2, 3, 4, 5))
        feedRepository.addFeed(2, listOf(2, 4))
        feedRepository.addFeed(3, listOf(2, 3))
        feedRepository.addFeed(4, listOf(2, 4, 5))

        val user1Feed = feedRepository.getFeed(userId = 1, lastPostId = 1000.0, 10)
        val user2Feed = feedRepository.getFeed(userId = 2, lastPostId = 1000.0, 10)
        val user3Feed = feedRepository.getFeed(userId = 3, lastPostId = 1000.0, 10)
        val user4Feed = feedRepository.getFeed(userId = 4, lastPostId = 1000.0, 10)
        val user5Feed = feedRepository.getFeed(userId = 5, lastPostId = 1000.0, 10)

        assertThat(user1Feed.size).isZero()
        assertThat(user2Feed).containsExactlyInAnyOrder("4", "3", "2", "1")
        assertThat(user3Feed).containsExactlyInAnyOrder("3", "1")
        assertThat(user4Feed).containsExactlyInAnyOrder("4", "2", "1")
        assertThat(user5Feed).containsExactlyInAnyOrder("4", "1")

        val feed = feedRepository.getFeed(userId = 2, 5.0, 2)
        assertThat(feed).containsExactlyInAnyOrder("4", "3")
    }

    @Test
    @DisplayName("피드 삭제 테스트")
    fun removeTest() {
        feedRepository.addFeed(1, listOf(1))
        feedRepository.addFeed(2, listOf(1))
        feedRepository.addFeed(3, listOf(1))

        val feed = feedRepository.getFeed(1, 100.0, 10)
        assertThat(feed).containsExactlyInAnyOrder("3", "2", "1")

        feedRepository.removeFeed(1, listOf(3, 1, 10))

        val feedAfterRemove = feedRepository.getFeed(1, 100.0, 10)
        assertThat(feedAfterRemove).containsExactlyInAnyOrder("2")
    }

}
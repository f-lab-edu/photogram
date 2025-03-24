package com.setung.consumer

import com.setung.client.PostServiceClient
import com.setung.client.UserServiceClient
import com.setung.kafka.event.Event
import com.setung.kafka.event.EventType
import com.setung.kafka.event.payload.*
import com.setung.repo.FeedRepository
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.Test

@SpringBootTest
class FeedEventConsumerTest {

    @Autowired
    lateinit var feedRepository: FeedRepository

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    lateinit var redisTemplate: StringRedisTemplate

    @MockitoBean
    private val postServiceClient: PostServiceClient = mock()

    @MockitoBean
    private val userServiceClient: UserServiceClient = mock()


    @BeforeEach
    fun beforeEach() {
        redisTemplate.execute { it.serverCommands().flushAll() }
    }

    @Test
    @DisplayName("게시글 업로드 이벤트 - 팔로우하고 있는 유저의 피드에 게시글 id 저장")
    fun postUploadedEvent() {
        kafkaTemplate.send(
            EventType.POST_UPLOADED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.POST_UPLOADED,
                payload = PostUploadedEventPayload(
                    postId = 1,
                    followerIds = listOf(1, 2)
                )
            ).toJson()
        )

        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val user1Feed = feedRepository.getFeed(1, Double.MAX_VALUE, 100)
            val user2Feed = feedRepository.getFeed(2, Double.MAX_VALUE, 100)

            assertThat(user1Feed.size).isEqualTo(1)
            assertThat(user2Feed.size).isEqualTo(1)
        }
    }

    @Test
    @DisplayName("게시글 삭제 이벤트 - 팔로우하고 있는 유저의 피드에 게시글 id 삭제")
    fun postDeleteEvent() {
        feedRepository.addFeed(listOf(1, 2, 3), 1)

        kafkaTemplate.send(
            EventType.POST_DELETED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.POST_DELETED,
                payload = PostDeletedEventPayload(
                    postId = 2,
                    followerIds = listOf(1)
                )
            ).toJson()
        )

        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val feed = feedRepository.getFeed(1, Double.MAX_VALUE, 100)
            assertThat(feed.size).isEqualTo(2)
        }
    }

    @Test
    @DisplayName("팔로우 이벤트 - 팔로우 후 피드에 게시글 id 저장")
    fun userFollowEvent() {
        Mockito.`when`(postServiceClient.findAllIdsByWriterId(2, 100))
            .thenReturn(listOf(1, 2, 3))

        kafkaTemplate.send(
            EventType.USER_FOLLOWED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.USER_FOLLOWED,
                payload = UserFollowedEventPayload(1, 2)
            ).toJson()
        )

        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val feed = feedRepository.getFeed(1, Double.MAX_VALUE, 100)
            assertThat(feed.size).isEqualTo(3)
        }
    }

    @Test
    @DisplayName("언팔로우 이벤트 - 언팔로우 후 피드에 게시글 id 삭제")
    fun userUnfollowEvent() {
        Mockito.`when`(postServiceClient.findAllIdsByWriterId(2, 100))
            .thenReturn(listOf(1, 2, 3))

        feedRepository.addFeed(listOf(1, 2, 3, 4, 5), 1)

        kafkaTemplate.send(
            EventType.USER_UNFOLLOWED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.USER_UNFOLLOWED,
                payload = UserUnfollowedEventPayload(1, 2)
            ).toJson()
        )

        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val feed = feedRepository.getFeed(1, Double.MAX_VALUE, 100)
            assertThat(feed.size).isEqualTo(2)
        }
    }

    @Test
    @DisplayName("계정 삭제 이벤트 - 계정 삭제 후 피드에 게시글 id 삭제")
    fun userDeleteEvent() {
        Mockito.`when`(postServiceClient.findAllIdsByWriterId(1, 100))
            .thenReturn(listOf(1, 2, 3))

        feedRepository.addFeed(listOf(1, 2, 3, 4, 5), 2)

        kafkaTemplate.send(
            EventType.USER_DELETED.topic,
            Event.Companion.of(
                eventId = UUID.randomUUID().toString(),
                type = EventType.USER_DELETED,
                payload = UserDeletedEventPayload(1, listOf(2))
            ).toJson()
        )

        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val feed = feedRepository.getFeed(2, Double.MAX_VALUE, 100)
            assertThat(feed.size).isEqualTo(2)
        }
    }
}
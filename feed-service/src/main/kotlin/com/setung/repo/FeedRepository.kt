package com.setung.repo

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class FeedRepository(
    private val redisTemplate: StringRedisTemplate
) {

    fun getFeed(userId: Long, lastPostId: Double, limit: Long): MutableSet<String> {
        return redisTemplate.opsForZSet().reverseRangeByScore(
            getUserFeedKey(userId),
            0.0,
            lastPostId - 1,
            0,
            limit
        )!!
    }

    fun addFeed(postId: Long, followerIds: List<Long>) {
        followerIds.forEach {
            redisTemplate.opsForZSet().add(getUserFeedKey(it), postId.toString(), postId.toDouble())
        }
    }

    fun addFeed(postIds: List<Long>, userId: Long) {
        postIds.forEach {
            redisTemplate.opsForZSet().add(getUserFeedKey(userId), it.toString(), it.toDouble())
        }
    }

    fun removeFeed(followerIds: List<Long>, postIds: List<Long>) {
        followerIds.forEach { removeFeed(it, postIds) }
    }

    fun removeFeed(userId: Long, postIds: List<Long>) {
        redisTemplate.opsForZSet().remove(
            getUserFeedKey(userId),
            *postIds.map { it.toString() }.toTypedArray()
        )
    }

    fun getUserFeedKey(userId: Long) = "user-feed:zset:$userId"

}
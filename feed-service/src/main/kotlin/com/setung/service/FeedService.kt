package com.setung.service

import com.setung.client.PostDetails
import com.setung.client.PostServiceClient
import com.setung.repo.FeedRepository
import org.springframework.stereotype.Service

@Service
class FeedService(
    private val feedRepository: FeedRepository,
    private val postServiceClient: PostServiceClient
) {

    fun getFeed(userId: Long, lastPostId: Double, limit: Long): List<PostDetails> {
        val postIds = feedRepository.getFeed(userId, lastPostId, limit)
        return postServiceClient.getPostsByIds(postIds.toList().map { it.toLong() })
    }


}


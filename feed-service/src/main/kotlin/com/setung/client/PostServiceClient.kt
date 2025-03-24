package com.setung.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime

@FeignClient("post-service")
@Component
interface PostServiceClient {

    @GetMapping("posts/writers/{writerId}/ids")
    fun findAllIdsByWriterId(
        @PathVariable writerId: Long,
        @RequestParam pageSize: Int
    ): List<Long>


    @GetMapping("/posts")
    fun getPostsByIds(@RequestParam postIds: List<Long>): List<PostDetails>
}

data class PostDetails(
    val id: Long,
    val writerId: Long,
    val contents: String?,
    val images: List<PostImageDetails>?,
    val postTags: List<PostTagDetails>?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {

}

data class PostImageDetails(
    val id: Long,
    val url: String
)

data class PostTagDetails(
    val id: Long,
    val name: String
)
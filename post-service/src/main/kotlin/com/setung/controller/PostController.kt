package com.setung.controller

import com.setung.auth.annotation.LoginUser
import com.setung.dto.PostUpdateRequest
import com.setung.dto.PostUploadRequest
import com.setung.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService
) {

    @PostMapping
    fun upload(
        @LoginUser loginUserId: Long,
        @RequestPart("request") request: PostUploadRequest,
        @RequestPart("images", required = false) images: List<MultipartFile> = emptyList()
    ) =
        ResponseEntity(postService.upload(loginUserId, request, images), HttpStatus.OK)

    @DeleteMapping("/{postId}")
    fun delete(@LoginUser loginUserId: Long, @PathVariable postId: Long) =
        ResponseEntity(postService.delete(loginUserId, postId), HttpStatus.OK)

    @PostMapping("/{postId}")
    fun update(
        @LoginUser loginUserId: Long,
        @PathVariable postId: Long,
        @RequestPart("request") request: PostUpdateRequest,
        @RequestPart("newImages", required = false) newImages: List<MultipartFile> = emptyList()
    ) =
        ResponseEntity(postService.update(loginUserId, postId, request, newImages), HttpStatus.OK)

    @GetMapping("/{postId}")
    fun findById(@LoginUser loginUserId: Long, @PathVariable postId: Long) =
        ResponseEntity(postService.findPost(loginUserId, postId), HttpStatus.OK)

    @GetMapping("/writers/{writerId}")
    fun findAllByWriterId(
        @LoginUser loginUserId: Long,
        @PathVariable writerId: Long,
        @RequestParam(required = false) lastPostId: Long?,
        @RequestParam(defaultValue = "10") pageSize: Int,
    ) =
        ResponseEntity(postService.findAllByWriterId(loginUserId, writerId, lastPostId, pageSize), HttpStatus.OK)

    @GetMapping("/writers/{writerId}/ids")
    fun findAllIdsByWriterId(
        @PathVariable writerId: Long,
        @RequestParam(defaultValue = "10") pageSize: Int
    ) =
        ResponseEntity(postService.findAllIdsByWriterId(writerId, pageSize), HttpStatus.OK)

    @GetMapping
    fun getPostsByIds(@RequestParam postIds: List<Long>) = ResponseEntity(postService.findAllByIds(postIds), HttpStatus.OK)

}


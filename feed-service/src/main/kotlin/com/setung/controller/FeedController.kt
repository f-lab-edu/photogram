package com.setung.controller

import com.setung.auth.annotation.LoginUser
import com.setung.service.FeedService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feed")
class FeedController(
    private val feedService: FeedService
) {

    @GetMapping
    fun getFeed(
        @LoginUser userId: Long,
        @RequestParam(required = false) lastPostId: Double = Double.MAX_VALUE,
        @RequestParam limit: Long
    ) =
        ResponseEntity(feedService.getFeed(userId, lastPostId, limit), HttpStatus.OK)
}
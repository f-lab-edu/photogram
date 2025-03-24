package com.setung.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import java.time.LocalDateTime

@FeignClient("user-service")
@Component
interface UserServiceClient {

    @GetMapping("/users/{userId}")
    fun getUser(@RequestHeader("user-id") loginUserId: Long, @PathVariable userId: Long): UserDto

}

data class UserDto(
    val id: Long,
    val email: String?,
    val name: String,
    val isVisible: Boolean,
    val biography: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
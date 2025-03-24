package com.setung.repo

import com.setung.entity.FollowEntity
import com.setung.entity.FollowStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FollowRepository : JpaRepository<FollowEntity, Long> {

    fun findByRequesterIdAndTargetId(requesterId: Long, targetId: Long): FollowEntity?

    @Query(value = "select f.requester.id from FollowEntity f where f.target.id = :userId AND f.status = :status")
    fun findFollowersByUserId(userId: Long, status: FollowStatus): List<Long>
}
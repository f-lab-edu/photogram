package com.setung.service

import com.setung.entity.FollowEntity
import com.setung.entity.FollowStatus
import com.setung.error.*
import com.setung.producer.UserEventProducer
import com.setung.repo.FollowRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val userService: UserService,
    private val userEventPublisher: UserEventProducer
) {

    fun follow(requesterId: Long, targetId: Long): Long {
        if (requesterId == targetId)
            throw SelfFollowException("cannot follow yourself")

        val requester = userService.findById(requesterId)
        val target = userService.findById(targetId)
        val status = if (target.isPrivate) FollowStatus.PENDING else FollowStatus.ACCEPTED

        return try {
            val follow = followRepository.save(FollowEntity(requester = requester, target = target, status = status))

            if (follow.status == FollowStatus.ACCEPTED)
                userEventPublisher.sendUserFollowEvent(requesterId, targetId)

            follow.id!!
        } catch (ex: DataIntegrityViolationException) {
            throw DuplicationException("Follow already exists.")
        }
    }

    fun findById(followId: Long): FollowEntity =
        followRepository.findById(followId).orElseThrow { NotFoundException("Could not find follow with id $followId") }

    fun acceptFollow(loginUserId: Long, followId: Long) {
        val follow = findById(followId)

        if (follow.target.id != loginUserId)
            throw ForbiddenException("Could not accept other's follow")
        if (follow.status != FollowStatus.PENDING)
            throw BadRequestException("Could not accept follow ${follow.status} status")

        follow.accept()
        userEventPublisher.sendUserFollowEvent(follow.requester.id!!, follow.target.id!!)

        followRepository.save(follow)
    }

    fun rejectFollow(loginUserId: Long, followId: Long) {
        val follow = findById(followId)

        if (follow.target.id != loginUserId)
            throw ForbiddenException("Could not reject other's follow")
        if (follow.status != FollowStatus.PENDING)
            throw BadRequestException("Could not reject follow ${follow.status} status")

        follow.reject()
        userEventPublisher.sendUserUnfollowEvent(follow.requester.id!!, follow.target.id!!)

        followRepository.save(follow)
    }

    fun deleteFollow(loginUserId: Long, followId: Long) {
        val follow = findById(followId)

        if (follow.requester.id != loginUserId)
            throw ForbiddenException("Could not delete other's follow")

        userEventPublisher.sendUserUnfollowEvent(follow.requester.id!!, follow.target.id!!)

        followRepository.delete(follow)
    }

    fun findByRequesterIdAndTargetId(requesterId: Long, targetId: Long): FollowEntity? {
        return followRepository.findByRequesterIdAndTargetId(requesterId, targetId)
    }
}
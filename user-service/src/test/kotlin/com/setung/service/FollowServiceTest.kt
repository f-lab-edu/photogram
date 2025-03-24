package com.setung.service

import com.setung.config.TestContainerConfig
import com.setung.entity.FollowStatus
import com.setung.error.*
import com.setung.repo.FollowRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestContainerConfig::class)
class FollowServiceTest @Autowired constructor(
    private val followService: FollowService,
    private val followRepository: FollowRepository
) {

    @Nested
    inner class FollowEntityTest {

        @Test
        @DisplayName("공개 계정 팔로우 성공 테스트")
        fun followSuccessTestWithPublicUser() {
            val followId = followService.follow(1, 7)
            val follow = followService.findById(followId)

            assertThat(follow.status).isEqualTo(FollowStatus.ACCEPTED)
            assertThrows<DuplicationException> { followService.follow(7, 1) }
        }

        @Test
        @DisplayName("비공개 계정 팔로우 성공 테스트")
        fun followSuccessTestWithPrivateUser() {
            val followId = followService.follow(1, 8)
            val follow = followService.findById(followId)

            assertThat(follow.status).isEqualTo(FollowStatus.PENDING)
            assertThrows<DuplicationException> { followService.follow(1, 8) }
        }

        @Test
        @DisplayName("팔로우 실패 테스트 - 셀프 팔로우")
        fun followFailureTestWithSelfFollow() {
            assertThrows<SelfFollowException> { followService.follow(1, 1) }
        }
    }

    @Nested
    inner class AcceptAndRejectTest {

        @Test
        @DisplayName("팔로우 요청 승인 성공 테스트")
        fun followAcceptSuccessTest() {
            followService.acceptFollow(7, 3)
            val follow = followService.findById(3)

            assertThat(follow.status).isEqualTo(FollowStatus.ACCEPTED)
        }

        @Test
        @DisplayName("팔로우 요청 승인 실패 테스트 - 다른 사용자의 팔로우")
        fun followAcceptFailureTestWithOthersFollow() {
            assertThrows<ForbiddenException> { followService.acceptFollow(1, 4) }
        }

        @Test
        @DisplayName("팔로우 요청 승인 실패 테스트 - PENDING 상태가 아닌 팔로우")
        fun followAcceptFailureTestWithoutPending() {
            assertThrows<BadRequestException> { followService.acceptFollow(8, 5) }
        }

        @Test
        @DisplayName("팔로우 요청 거절 성공 테스트")
        fun followRejectSuccessTest() {
            followService.rejectFollow(8, 6)
            val follow = followService.findById(6)

            assertThat(follow.status).isEqualTo(FollowStatus.REJECTED)
        }

        @Test
        @DisplayName("팔로우 요청 거절 실패 테스트 - 다른 사용자의 팔로우")
        fun followRejectFailureTestWithOthersFollow() {
            assertThrows<ForbiddenException> { followService.rejectFollow(1, 4) }
        }

        @Test
        @DisplayName("팔로우 요청 거절 실패 테스트 - PENDING 상태가 아닌 팔로우")
        fun followRejectFailureTestWithoutPending() {
            assertThrows<BadRequestException> { followService.rejectFollow(8, 5) }
        }
    }

    @Nested
    inner class DeleteTest {

        @Test
        @DisplayName("팔로우 삭제 성공 테스트")
        fun deleteFollowSuccessTest() {
            followService.deleteFollow(1, 7)

            assertThrows<NotFoundException> { followService.findById(7) }
        }

        @Test
        @DisplayName("팔로우 삭제 실패 테스트 - 다른 사용자의 팔로우")
        fun deleteFollowFailureTestWithOthers() {
            assertThrows<ForbiddenException> { followService.deleteFollow(1, 5) }
        }
    }

    @Nested
    inner class FindFollowerIds {

        @Test
        fun test() {
            val acceptedFollowers = followRepository.findFollowersByUserId(11, FollowStatus.ACCEPTED)
            val pendingFollowers = followRepository.findFollowersByUserId(11, FollowStatus.PENDING)
            val rejectedFollowers = followRepository.findFollowersByUserId(11, FollowStatus.REJECTED)

            assertThat(acceptedFollowers).contains(1L)
            assertThat(pendingFollowers).isEmpty()
            assertThat(rejectedFollowers).isEmpty()
        }
    }
}

package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.UserDTO
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserFollow
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.repository.user.UserFollowRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkField
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserFollowService(
    private val userRepository: UserRepository,
    private val userFollowRepository: UserFollowRepository,
) {
    private companion object {
        private const val PAGE_SIZE = 10
    }

    @Transactional(readOnly = true)
    fun getFollowers(followeeId: Long, page: Int): Page<UserDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        val followee = userRepository.getReferenceById(followeeId)
        return userFollowRepository.findFollowersByFolloweeOrderByIdDesc(
            followee = followee,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(UserMapper::map)
    }

    @Transactional(readOnly = true)
    fun getFollowees(followerId: Long, page: Int): Page<UserDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        val follower = userRepository.getReferenceById(followerId)
        return userFollowRepository.findFolloweesByFollowerOrderByIdDesc(
            follower = follower,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(UserMapper::map)
    }

    @Transactional(readOnly = true)
    fun existsFollow(followeeId: Long, follower: User): Boolean {
        val followee = userRepository.getReferenceById(followeeId)
        return userFollowRepository.existsByFollowerAndFollowee(follower, followee)
    }

    @Transactional
    fun followUser(followeeId: Long, follower: User): UserFollow {
        val followee = userRepository.getReferenceById(followeeId)

        checkConflict(userFollowRepository.existsByFollowerAndFollowee(follower, followee)) {
            ResponseMessage.User.Follow.ALREADY_EXISTS
        }

        val newFollow = UserFollow(
            follower = follower,
            followee = followee,
        )
        return userFollowRepository.save(newFollow)
    }

    @Transactional
    fun unfollowUser(followeeId: Long, follower: User) {
        val followee = userRepository.getReferenceById(followeeId)

        val follow = userFollowRepository.findByFollowerAndFollowee(follower, followee)
            ?: throw EntityNotFoundException(ResponseMessage.User.Follow.NOT_FOUND)

        userFollowRepository.delete(follow)
    }
}
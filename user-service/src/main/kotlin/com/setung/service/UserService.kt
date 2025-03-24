package com.setung.service

import com.setung.auth.constant.LoginStatus
import com.setung.auth.jwt.JwtProvider
import com.setung.dto.*
import com.setung.entity.*
import com.setung.error.DuplicateEmailException
import com.setung.error.InvalidPasswordException
import com.setung.error.NotFoundException
import com.setung.file.FileClient
import com.setung.mail.EmailClient
import com.setung.producer.UserEventProducer
import com.setung.repo.FollowRepository
import com.setung.repo.ProfileImageRepository
import com.setung.repo.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val emailCodeService: EmailCodeService,
    val emailService: EmailClient,
    val jwtProvider: JwtProvider,
    val fileClient: FileClient,
    val profileImageRepository: ProfileImageRepository,
    private val followRepository: FollowRepository,
    private val userEventPublisher: UserEventProducer
) {

    fun signup(request: UserSignupRequest): Long {
        if (userRepository.existsByEmailAndStatus(
                request.email,
                UserStatus.NORMAL
            )
        ) throw DuplicateEmailException(request.email)

        emailCodeService.verifyEmailCode(request.email, request.code, EmailCodeType.SIGNUP)

        val user: UserEntity = UserEntity.of(request, passwordEncoder.encode(request.password))

        return userRepository.save(user).id ?: throw IllegalStateException("Failed to save user")
    }

    fun sendEmailCode(request: SendEmailCodeRequest) {
        val code = emailCodeService.generateEmailCode(request.email, request.type)
        emailService.send(request.email, code)
    }

    fun login(request: LoginRequest): String {
        val user = userRepository.findByEmailAndStatus(request.email, UserStatus.NORMAL)
            ?: throw NotFoundException("Could not find user with email ${request.email}")

        if (!passwordEncoder.matches(request.password, user.password))
            throw InvalidPasswordException()

        return jwtProvider.createToken(user.id!!)
    }

    fun update(id: Long, request: UserUpdateRequest) {
        val user = findById(id)
        user.update(request)
        userRepository.save(user)
    }

    fun updatePassword(id: Long, request: PasswordUpdateRequest) {
        val user = findById(id)
        val encryptedPassword = passwordEncoder.encode(request.password)
        emailCodeService.verifyEmailCode(user.email, request.code, EmailCodeType.PASSWORD_RESET)

        user.updatePassword(encryptedPassword)

        userRepository.save(user)
    }

    fun delete(id: Long, request: UserDeleteRequest) {
        val user = findById(id)
        emailCodeService.verifyEmailCode(user.email, request.code, EmailCodeType.ACCOUNT_DELETE)
        userEventPublisher.sendUserDeleteEvent(user.id!!, getFollowers(id, FollowStatus.ACCEPTED))

        user.delete()

        userRepository.save(user)
    }

    fun findMe(id: Long) =
        UserDto.ofPublicUser(findById(id))

    fun findUser(loginUserId: Long = LoginStatus.ANONYMOUS.id, id: Long): UserDto {
        val user = findById(id)
        if (user.isPrivate && loginUserId != id) {
            if (loginUserId == LoginStatus.ANONYMOUS.id) {
                return UserDto.ofPrivateUser(user)
            } else {
                val follow = followRepository.findByRequesterIdAndTargetId(loginUserId, id)
                if (follow == null || follow.status != FollowStatus.ACCEPTED)
                    return UserDto.ofPrivateUser(user)
            }
        }

        return UserDto.ofPublicUser(findById(id))
    }

    fun findById(id: Long) =
        userRepository.findByIdAndStatus(id, UserStatus.NORMAL)
            ?: throw NotFoundException("Could not find user with id ${id}")

    @Transactional
    fun uploadProfileImage(userId: Long, image: MultipartFile) {
        val url = fileClient.upload(image)
        val profileImage = profileImageRepository.save(ProfileImage(url = url, fileName = image.originalFilename!!))
        val user = findById(userId)

        user.uploadProfileImage(profileImage)
    }

    @Transactional
    fun deleteProfileImage(userId: Long) {
        val user = findById(userId)
        if (user.profileImage != null) {
            fileClient.delete(user.profileImage!!.url)
            user.deleteProfileImage()
        }
    }

    fun getFollowers(userId: Long, status: FollowStatus): List<Long> = followRepository.findFollowersByUserId(userId, status)

}
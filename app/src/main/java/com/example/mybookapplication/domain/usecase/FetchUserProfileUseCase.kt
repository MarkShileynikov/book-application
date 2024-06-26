package com.example.mybookapplication.domain.usecase

import com.example.mybookapplication.domain.entity.UserProfile
import com.example.mybookapplication.domain.repository.UserRepository
import com.example.mybookapplication.domain.util.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchUserProfileUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke() : Flow<Event<UserProfile>> = flow {
        val userProfile = userRepository.fetchUserProfile()
        emit(userProfile)
    }
}
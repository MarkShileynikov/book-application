package com.example.mybookapplication.data.repository

import com.example.mybookapplication.data.prefs.PrefsDataSource
import com.example.mybookapplication.domain.entity.UserProfile
import com.example.mybookapplication.domain.entity.isValid
import com.example.mybookapplication.domain.repository.UserRepository
import com.example.mybookapplication.domain.util.Event
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val prefsDataSource: PrefsDataSource) : UserRepository {

    override suspend fun fetchUserProfile(): Event<UserProfile> {
        val userProfile = prefsDataSource.fetchUserProfile()
        return if (userProfile.isValid()) {
            Event.Success(userProfile)
        } else {
            Event.Failure("User is not valid")
        }
    }

    override suspend fun deleteUserProfile() {
        prefsDataSource.deleteUserProfile()
    }
}
package com.example.mybookapplication.domain.repository

import com.example.mybookapplication.domain.entity.UserProfile
import com.example.mybookapplication.domain.util.Event

interface UserRepository {

    suspend fun fetchUserProfile() : Event<UserProfile>

    suspend fun deleteUserProfile()

}
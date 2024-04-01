package com.example.mybookapplication.presentation.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mybookapplication.App
import com.example.mybookapplication.data.prefs.PrefsDataSourceImpl
import com.example.mybookapplication.data.repository.UserRepositoryImpl
import com.example.mybookapplication.domain.entity.UserProfile
import com.example.mybookapplication.domain.usecase.FetchUserProfileUseCase
import com.example.mybookapplication.domain.util.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProfileViewModel(context : Application, private val fetchUserProfileUseCase: FetchUserProfileUseCase) : AndroidViewModel(context) {
    val viewState = MutableStateFlow<Event<UserProfile>>(Event.Failure("No profile found"))

    fun fetchUserProfile() {
        viewModelScope.launch {
            fetchUserProfileUseCase()
                .catch {
                    viewState.value = Event.Failure("No profile found")
                }
                .collect { userProfile ->
                    viewState.value = userProfile
                }
        }
    }
    companion object {
        val profileViewModelFactory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val prefsDataSource = PrefsDataSourceImpl(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                val userRepository = UserRepositoryImpl(prefsDataSource)
                val fetchUserProfileUseCase = FetchUserProfileUseCase(userRepository)
                return@initializer ProfileViewModel(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App,
                    fetchUserProfileUseCase
                )
            }
        }
    }
}
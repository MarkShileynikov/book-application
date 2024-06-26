package com.example.mybookapplication.presentation.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybookapplication.domain.usecase.FetchSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    context : Application,
    private val fetchSessionUseCase: FetchSessionUseCase
) : AndroidViewModel(context) {
    val viewState = MutableStateFlow<SplashViewState>(SplashViewState.Loading)

    init {
        verifySession()
    }
    private fun verifySession() {
        viewModelScope.launch {
            fetchSessionUseCase()
                .onStart { delay(1000) }
                .catch {
                    viewState.value =
                        SplashViewState.Failure
                }
                .collect {token ->
                    viewState.value = if (token.isNullOrEmpty()) {
                        SplashViewState.Failure
                    }
                    else {
                        SplashViewState.Success
                    }

                }
        }
    }
}
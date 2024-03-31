package com.example.mybookapplication.presentation.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mybookapplication.App
import com.example.mybookapplication.data.api.NetworkClient
import com.example.mybookapplication.data.prefs.PrefsDataSourceImpl
import com.example.mybookapplication.data.repository.SessionRepositoryImpl
import com.example.mybookapplication.domain.usecase.SignInUseCase
import com.example.mybookapplication.domain.usecase.SignUpUseCase
import com.example.mybookapplication.presentation.signin.SignInViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SignUpViewModel(context : Application, private val signUpUseCase: SignUpUseCase, private val signInUseCase: SignInUseCase) : AndroidViewModel(context) {
    val viewState = MutableStateFlow<SignUpViewState>(SignUpViewState.Idle)

    fun onSignUpButtonClicked(email: String, password : String, username : String) {
        viewModelScope.launch {
            signUpUseCase(SignUpUseCase.Params(email, password, username))
                .onStart { viewState.value = SignUpViewState.Loading }
                .catch {
                    viewState.value = SignUpViewState.Failure(it.message ?: "Something went wrong.")
                }
                .collect { _ ->
                    signIn(email, password)
                }
        }
    }

    private suspend fun signIn(email: String, password: String) {
        viewModelScope.launch {
            signInUseCase(SignInUseCase.Params(email, password))
                .onStart { viewState.value = SignUpViewState.Loading }
                .catch { viewState.value = SignUpViewState.Failure(it.message ?: "Something went wrong.") }
                .collect {_ ->
                    viewState.value = SignUpViewState.Success
                }
        }
    }

    fun isPasswordValid(password: String) : Boolean{
        val passwordRegex = Regex("^(?=.*[A-Za-z]).{8,}\$")
        return passwordRegex.matches(password)
    }

    companion object {
        val signUpViewModelFactory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                val authApiService = NetworkClient.provideAuthApiService()
                val prefsDataSource = PrefsDataSourceImpl(context)
                val sessionRepository = SessionRepositoryImpl(context, authApiService, prefsDataSource)
                val signUpUseCase = SignUpUseCase(context, sessionRepository)
                val signInUseCase = SignInUseCase(context, sessionRepository)
                return@initializer SignUpViewModel(
                    context, signUpUseCase, signInUseCase
                )
            }
        }
    }

}
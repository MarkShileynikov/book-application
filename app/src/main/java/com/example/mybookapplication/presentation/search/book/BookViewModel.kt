package com.example.mybookapplication.presentation.search.book

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybookapplication.R
import com.example.mybookapplication.domain.entity.Review
import com.example.mybookapplication.domain.entity.UserProfile
import com.example.mybookapplication.domain.usecase.FetchReviewsUseCase
import com.example.mybookapplication.domain.usecase.FetchUserProfileUseCase
import com.example.mybookapplication.domain.util.Event
import com.example.mybookapplication.presentation.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    context : Application,
    private val fetchUserProfileUseCase: FetchUserProfileUseCase,
    private val fetchReviewsUseCase: FetchReviewsUseCase,
): AndroidViewModel(context) {
    val userProfileViewState = MutableStateFlow<Event<UserProfile>>(Event.Failure("No profile found"))
    val reviewViewState = MutableStateFlow<ViewState<List<Review>>>(ViewState.Loading)
    private var bookId: String = ""
    init {
        fetchUserProfile()
    }

    fun setBookId(bookId: String) {
        this.bookId = bookId
    }

    fun fetchReviews() {
        viewModelScope.launch {
            fetchReviewsUseCase(bookId)
                .onStart { reviewViewState.value = ViewState.Loading }
                .catch { reviewViewState.value = ViewState.Failure(it.message ?: "No reviews found") }
                .collect {reviews ->
                    reviewViewState.value = ViewState.Success(reviews)
                }
        }
    }

    fun pluralizeReaders(context: Context, count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> context.resources.getQuantityString(R.plurals.readers_singular, count, count)
            count % 10 in 2..4 && count % 100 !in 12..14 -> context.resources.getQuantityString(R.plurals.readers_plural, count, count)
            else -> context.resources.getQuantityString(R.plurals.readers_plural, count, count)
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            fetchUserProfileUseCase()
                .catch {
                    userProfileViewState.value = Event.Failure("No profile found")
                }
                .collect { userProfile ->
                    userProfileViewState.value = userProfile
                }
        }
    }
}
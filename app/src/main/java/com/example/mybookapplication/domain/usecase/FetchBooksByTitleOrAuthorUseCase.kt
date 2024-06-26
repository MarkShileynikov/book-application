package com.example.mybookapplication.domain.usecase

import com.example.mybookapplication.domain.entity.Book
import com.example.mybookapplication.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchBooksByTitleOrAuthorUseCase @Inject constructor(private val bookRepository: BookRepository) {

    suspend operator fun invoke(titleOrAuthor : String): Flow<List<Book>> = bookRepository.fetchBooksByGenreOrAuthor(titleOrAuthor)
}
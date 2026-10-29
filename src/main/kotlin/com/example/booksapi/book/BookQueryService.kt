package com.example.booksapi.book

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookQueryService(
    private val bookRepository: BookRepository,
) {
    @Transactional(readOnly = true)
    fun findPage(page: Int, size: Int): List<Book> {
        require(page >= 0) { "Page must be non-negative" }
        require(size in 1..100) { "Size must be between 1 and 100" }
        return bookRepository.findAll(
            PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")),
        ).content
    }

    @Transactional(readOnly = true)
    fun findById(id: String): Book? = bookRepository.findById(id).orElse(null)
}

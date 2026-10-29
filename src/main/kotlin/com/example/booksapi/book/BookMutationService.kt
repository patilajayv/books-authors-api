package com.example.booksapi.book

import com.example.booksapi.author.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class BookMutationService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {
    @Transactional
    fun addBook(input: BookInput): Book {
        val author = authorRepository.findById(input.authorId)
            .orElseThrow { IllegalArgumentException("Author was not found") }
        return bookRepository.save(Book(UUID.randomUUID().toString(), input.title, author))
    }

    @Transactional
    fun updateBook(id: String, input: UpdateBookInput): Book {
        val book = bookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Book was not found") }

        if (!input.title.isOmitted) {
            book.title = input.title.value()
                ?: throw IllegalArgumentException("Book title cannot be null")
        }

        if (!input.authorId.isOmitted) {
            val authorId = input.authorId.value()
                ?: throw IllegalArgumentException("Book author cannot be null")
            book.author = authorRepository.findById(authorId)
                .orElseThrow { IllegalArgumentException("Author was not found") }
        }

        return bookRepository.save(book)
    }

    @Transactional
    fun deleteBook(id: String): Boolean {
        val book = bookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Book was not found") }
        bookRepository.delete(book)
        return true
    }
}

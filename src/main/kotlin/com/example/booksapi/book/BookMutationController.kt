package com.example.booksapi.book

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class BookMutationController(
    private val bookMutationService: BookMutationService,
) {
    @MutationMapping
    fun addBook(@Argument input: BookInput): Book = bookMutationService.addBook(input)

    @MutationMapping
    fun updateBook(
        @Argument id: String,
        @Argument input: UpdateBookInput,
    ): Book = bookMutationService.updateBook(id, input)

    @MutationMapping
    fun deleteBook(@Argument id: String): Boolean = bookMutationService.deleteBook(id)
}

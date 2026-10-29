package com.example.booksapi.book

import com.example.booksapi.author.Author
import com.example.booksapi.author.AuthorQueryService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class BooksQueryController(
    private val bookQueryService: BookQueryService,
    private val authorQueryService: AuthorQueryService,
) {
    @QueryMapping
    fun books(
        @Argument page: Int,
        @Argument size: Int,
    ): List<Book> = bookQueryService.findPage(page, size)

    @QueryMapping
    fun bookById(@Argument id: String): Book? = bookQueryService.findById(id)

    @QueryMapping
    fun authors(): List<Author> = authorQueryService.findAll()

    @BatchMapping(typeName = "Book", field = "author")
    fun bookAuthors(books: List<Book>): Map<Book, Author> {
        val authorsById = authorQueryService.findAllByIds(books.map { it.author.id })
            .associateBy(Author::id)
        return books.associateWith { book ->
            authorsById[book.author.id]
                ?: throw IllegalStateException("Book references a missing Author")
        }
    }
}

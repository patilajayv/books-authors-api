package com.example.booksapi

import com.example.booksapi.author.Author
import com.example.booksapi.author.AuthorRepository
import com.example.booksapi.book.Book
import com.example.booksapi.book.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.GraphQlTester

@SpringBootTest
@AutoConfigureGraphQlTester
class BooksQueryTests {
    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun setUp() {
        bookRepository.deleteAllInBatch()
        authorRepository.deleteAllInBatch()

        val firstAuthor = authorRepository.save(Author(id = "author-1", name = "Author One"))
        val secondAuthor = authorRepository.save(Author(id = "author-2", name = "Author Two"))
        bookRepository.save(Book(id = "1", title = "The Hobbit", author = firstAuthor))
        bookRepository.save(Book(id = "2", title = "Pride and Prejudice", author = secondAuthor))
    }

    @Test
    fun `books query returns persisted books and nested authors`() {
        val actual = graphQlTester
            .document(
                """
                query {
                    books {
                        id
                        title
                        author {
                            id
                            name
                        }
                    }
                }
                """.trimIndent(),
            )
            .execute()
            .path("books")
            .entityList(BookResult::class.java)
            .get()
            .sortedBy(BookResult::id)

        assertEquals(
            listOf(
                BookResult("1", "The Hobbit", AuthorResult("author-1", "Author One")),
                BookResult("2", "Pride and Prejudice", AuthorResult("author-2", "Author Two")),
            ),
            actual,
        )
    }

    @Test
    fun `bookById returns the persisted book and its author`() {
        val actual = graphQlTester
            .document(
                """
                query(${'$'}id: ID!) {
                    bookById(id: ${'$'}id) {
                        id
                        title
                        author { id name }
                    }
                }
                """.trimIndent(),
            )
            .variable("id", "1")
            .execute()
            .path("bookById")
            .entity(BookResult::class.java)
            .get()

        assertEquals(
            BookResult("1", "The Hobbit", AuthorResult("author-1", "Author One")),
            actual,
        )
    }

    @Test
    fun `authors query returns persisted authors`() {
        val actual = graphQlTester
            .document("query { authors { id name } }")
            .execute()
            .path("authors")
            .entityList(AuthorResult::class.java)
            .get()
            .sortedBy(AuthorResult::id)

        assertEquals(
            listOf(
                AuthorResult("author-1", "Author One"),
                AuthorResult("author-2", "Author Two"),
            ),
            actual,
        )
    }

    @Test
    fun `books and authors queries return empty lists when no records exist`() {
        bookRepository.deleteAllInBatch()
        authorRepository.deleteAllInBatch()

        val books = graphQlTester
            .document("query { books { id title author { id name } } }")
            .execute()
            .path("books")
            .entityList(BookResult::class.java)
            .get()
        val authors = graphQlTester
            .document("query { authors { id name } }")
            .execute()
            .path("authors")
            .entityList(AuthorResult::class.java)
            .get()

        assertEquals(emptyList<BookResult>(), books)
        assertEquals(emptyList<AuthorResult>(), authors)
    }

    @Test
    fun `multiple books can persist and resolve the same author`() {
        val sharedAuthor = authorRepository.findById("author-1").orElseThrow()
        bookRepository.save(Book(id = "3", title = "Another book", author = sharedAuthor))

        val books = graphQlTester
            .document(
                """
                query {
                    books {
                        id
                        author { id name }
                    }
                }
                """.trimIndent(),
            )
            .execute()
            .path("books")
            .entityList(BookAuthorResult::class.java)
            .get()

        assertEquals(2, books.count { it.author.id == "author-1" })
    }

    @Test
    fun `database rejects a book that references an unknown author`() {
        assertThrows(DataIntegrityViolationException::class.java) {
            bookRepository.saveAndFlush(
                Book(
                    id = "invalid-book",
                    title = "Invalid relationship",
                    author = Author(id = "missing-author", name = "Missing"),
                ),
            )
        }
    }

    data class BookResult(val id: String, val title: String, val author: AuthorResult)

    data class BookAuthorResult(val id: String, val author: AuthorResult)

    data class AuthorResult(val id: String, val name: String)
}

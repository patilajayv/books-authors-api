package com.example.booksapi

import com.example.booksapi.author.Author
import com.example.booksapi.author.AuthorRepository
import com.example.booksapi.book.Book
import com.example.booksapi.book.BookRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.GraphQlTester

@SpringBootTest
@AutoConfigureGraphQlTester
class BooksMutationTests {
    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    private lateinit var firstAuthor: Author
    private lateinit var secondAuthor: Author

    @BeforeEach
    fun setUp() {
        bookRepository.deleteAllInBatch()
        authorRepository.deleteAllInBatch()
        firstAuthor = authorRepository.save(Author(id = "author-1", name = "Author One"))
        secondAuthor = authorRepository.save(Author(id = "author-2", name = "Author Two"))
        bookRepository.save(Book(id = "book-1", title = "Original title", author = firstAuthor))
    }

    @Test
    fun `addBook persists a book for an existing author and returns its relationship`() {
        val result = graphQlTester
            .document(
                """
                mutation {
                    addBook(input: { title: "New title", authorId: "author-2" }) {
                        id title author { id name }
                    }
                }
                """.trimIndent(),
            )
            .execute()
            .path("addBook")
            .entity(BookResult::class.java)
            .get()

        assertEquals("New title", result.title)
        assertEquals(AuthorResult("author-2", "Author Two"), result.author)
        assertTrue(bookRepository.existsById(result.id))
        assertEquals(2, authorRepository.count())
    }

    @Test
    fun `addBook rejects an unknown author without creating data`() {
        val response = graphQlTester
            .document(
                """
                mutation {
                    addBook(input: { title: "New title", authorId: "missing-author" }) { id }
                }
                """.trimIndent(),
            )
            .execute()

        assertSafeGraphQlError(response)
        assertEquals(1, bookRepository.count())
        assertEquals(2, authorRepository.count())
    }

    @Test
    fun `updateBook changes title and preserves author when author id is omitted`() {
        val result = graphQlTester
            .document(
                """
                mutation {
                    updateBook(id: "book-1", input: { title: "Updated title" }) {
                        id title author { id name }
                    }
                }
                """.trimIndent(),
            )
            .execute()
            .path("updateBook")
            .entity(BookResult::class.java)
            .get()

        assertEquals("Updated title", result.title)
        assertEquals(AuthorResult("author-1", "Author One"), result.author)
    }

    @Test
    fun `updateBook changes author and preserves title when title is omitted`() {
        val result = graphQlTester
            .document(
                """
                mutation {
                    updateBook(id: "book-1", input: { authorId: "author-2" }) {
                        id title author { id name }
                    }
                }
                """.trimIndent(),
            )
            .execute()
            .path("updateBook")
            .entity(BookResult::class.java)
            .get()

        assertEquals("Original title", result.title)
        assertEquals(AuthorResult("author-2", "Author Two"), result.author)
    }

    @Test
    fun `updateBook rejects explicit null author id without clearing relationship`() {
        val response = graphQlTester
            .document(
                """
                mutation(${'$'}input: UpdateBookInput!) {
                    updateBook(id: "book-1", input: ${'$'}input) { id }
                }
                """.trimIndent(),
            )
            .variable("input", mapOf("authorId" to null))
            .execute()

        assertSafeGraphQlError(response)
        val persisted = bookRepository.findById("book-1").orElseThrow()
        assertEquals("author-1", persisted.author.id)
    }

    @Test
    fun `updateBook rejects explicit null title without changing book`() {
        val response = graphQlTester
            .document(
                """
                mutation(${'$'}input: UpdateBookInput!) {
                    updateBook(id: "book-1", input: ${'$'}input) { id }
                }
                """.trimIndent(),
            )
            .variable("input", mapOf("title" to null))
            .execute()

        assertSafeGraphQlError(response)
        assertEquals("Original title", bookRepository.findById("book-1").orElseThrow().title)
    }

    @Test
    fun `updateBook rejects unknown author id without changing book`() {
        val response = graphQlTester
            .document(
                """
                mutation {
                    updateBook(id: "book-1", input: { authorId: "missing-author" }) { id }
                }
                """.trimIndent(),
            )
            .execute()

        assertSafeGraphQlError(response)
        val persisted = bookRepository.findById("book-1").orElseThrow()
        assertEquals("Original title", persisted.title)
        assertEquals("author-1", persisted.author.id)
    }

    @Test
    fun `updateBook rejects unknown book id`() {
        val response = graphQlTester
            .document(
                """
                mutation {
                    updateBook(id: "missing-book", input: { title: "Updated" }) { id }
                }
                """.trimIndent(),
            )
            .execute()

        assertSafeGraphQlError(response)
        assertEquals(1, bookRepository.count())
    }

    @Test
    fun `deleteBook removes only the book and returns true`() {
        val deleted = graphQlTester
            .document("mutation { deleteBook(id: \"book-1\") }")
            .execute()
            .path("deleteBook")
            .entity(Boolean::class.java)
            .get()

        assertTrue(deleted)
        assertFalse(bookRepository.existsById("book-1"))
        assertTrue(authorRepository.existsById("author-1"))
    }

    @Test
    fun `deleteBook rejects unknown book id`() {
        val response = graphQlTester
            .document("mutation { deleteBook(id: \"missing-book\") }")
            .execute()

        assertSafeGraphQlError(response)
        assertTrue(authorRepository.existsById("author-1"))
    }

    private fun assertSafeGraphQlError(response: GraphQlTester.Response) {
        response.errors().satisfy { errors ->
            assertTrue(errors.isNotEmpty())
            assertFalse(errors.any { error ->
                val message = error.message.orEmpty()
                message.contains("was not found", ignoreCase = true) ||
                    message.contains("Exception", ignoreCase = true) ||
                    message.contains("com.example.booksapi") ||
                    message.contains("\n\tat ")
            })
        }
    }

    data class BookResult(val id: String, val title: String, val author: AuthorResult)

    data class AuthorResult(val id: String, val name: String)
}

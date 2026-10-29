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
class BooksPaginationAndNPlusOneTests {
    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @BeforeEach
    fun setUp() {
        replaceFixture(3)
    }

    @Test
    fun `books returns first and subsequent pages in stable id order without duplicates`() {
        val firstPage = booksPage(page = 0, size = 2)
        val secondPage = booksPage(page = 1, size = 2)

        assertEquals(listOf("book-001", "book-002"), firstPage.map(BookSummary::id))
        assertEquals(listOf("book-003"), secondPage.map(BookSummary::id))
        val combinedIds = firstPage.map(BookSummary::id) + secondPage.map(BookSummary::id)
        assertEquals(combinedIds.sorted(), combinedIds)
        assertEquals(combinedIds.size, combinedIds.distinct().size)
    }

    @Test
    fun `books returns an empty list beyond the last page`() {
        assertEquals(emptyList<BookSummary>(), booksPage(page = 2, size = 2))
    }

    @Test
    fun `books rejects invalid page and size bounds`() {
        assertGraphQlError("query { books(page: -1, size: 2) { id } }")
        assertGraphQlError("query { books(page: 0, size: 0) { id } }")
        assertGraphQlError("query { books(page: 0, size: 101) { id } }")
    }

    @Test
    fun `books with authors does not show one relationship query per additional book`() {
        val small = measureBooksAndAuthors(resultSize = 5)
        val large = measureBooksAndAuthors(resultSize = 20)

        assertEquals(5, small.returnedBooks)
        assertEquals(20, large.returnedBooks)
        assertEquals(1, small.rootBookFetches)
        assertEquals(1, large.rootBookFetches)
        assertTrue(small.authorRelationshipLoads > 0)
        assertTrue(large.authorRelationshipLoads > 0)
        assertEquals(0, small.otherStatements)
        assertEquals(0, large.otherStatements)

        val deltaBooks = large.returnedBooks - small.returnedBooks
        val deltaAuthorLoads = large.authorRelationshipLoads - small.authorRelationshipLoads
        println(
            "M5 SQL evidence: N_small=${small.returnedBooks}, rootBookFetches_small=${small.rootBookFetches}, " +
                "R_small=${small.authorRelationshipLoads}; N_large=${large.returnedBooks}, " +
                "rootBookFetches_large=${large.rootBookFetches}, R_large=${large.authorRelationshipLoads}; " +
                "deltaN=$deltaBooks, deltaR=$deltaAuthorLoads",
        )
        assertFalse(
            deltaAuthorLoads == deltaBooks,
            "Author relationship SQL grew one-for-one with returned Books; N+1 verification is not passed",
        )
    }

    private fun booksPage(page: Int, size: Int): List<BookSummary> = graphQlTester
        .document("query { books(page: $page, size: $size) { id title } }")
        .execute()
        .path("books")
        .entityList(BookSummary::class.java)
        .get()

    private fun assertGraphQlError(document: String) {
        val response = graphQlTester.document(document).execute()
        response.errors().satisfy { errors -> assertTrue(errors.isNotEmpty()) }
    }

    private fun measureBooksAndAuthors(resultSize: Int): SqlEvidence {
        replaceFixture(resultSize)
        val execution = SqlCaptureStatementInspector.measure {
            graphQlTester.document(RELATIONSHIP_QUERY).execute()
        }
        val books = execution.result
            .path("books")
            .entityList(BookWithAuthor::class.java)
            .get()
        val rootBookFetches = execution.statements.count { it.isSelectFrom("books") }
        val authorLoads = execution.statements.count { it.isSelectFrom("authors") }
        val otherStatements = execution.statements.size - rootBookFetches - authorLoads

        return SqlEvidence(
            returnedBooks = books.size,
            rootBookFetches = rootBookFetches,
            authorRelationshipLoads = authorLoads,
            otherStatements = otherStatements,
        )
    }

    private fun replaceFixture(size: Int) {
        bookRepository.deleteAllInBatch()
        authorRepository.deleteAllInBatch()
        val authors = (1..size).map { index ->
            Author(id = "author-${index.toString().padStart(3, '0')}", name = "Shared title")
        }
        authorRepository.saveAllAndFlush(authors)
        bookRepository.saveAllAndFlush(
            authors.mapIndexed { index, author ->
                Book(
                    id = "book-${(index + 1).toString().padStart(3, '0')}",
                    title = "Shared title",
                    author = author,
                )
            },
        )
    }

    private fun String.isSelectFrom(table: String): Boolean {
        val normalized = lowercase()
        return normalized.startsWith("select") && Regex("\\bfrom\\s+\\\"?$table\\\"?\\b").containsMatchIn(normalized)
    }

    data class BookSummary(val id: String, val title: String)

    data class BookWithAuthor(val id: String, val author: AuthorSummary)

    data class AuthorSummary(val id: String, val name: String)

    data class SqlEvidence(
        val returnedBooks: Int,
        val rootBookFetches: Int,
        val authorRelationshipLoads: Int,
        val otherStatements: Int,
    )

    companion object {
        private const val RELATIONSHIP_QUERY =
            "query { books(page: 0, size: 100) { id author { id name } } }"
    }
}

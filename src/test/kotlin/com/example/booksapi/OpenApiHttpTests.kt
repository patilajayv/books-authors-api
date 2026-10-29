package com.example.booksapi

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.booksapi.author.Author
import com.example.booksapi.author.AuthorRepository
import com.example.booksapi.book.BookRepository

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("default")
class OpenApiHttpTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var bookRepository: BookRepository

    @Test
    fun `openapi endpoint returns metadata and a valid document`() {
        val response = mockMvc.perform(get("/v3/api-docs")).andReturn().response

        assertEquals(200, response.status)
        assertTrue(response.contentType.orEmpty().startsWith(APPLICATION_JSON_VALUE))
        val document = objectMapper.readTree(response.contentAsString)
        assertTrue(document.path("openapi").isTextual)
        assertEquals("Books & Authors API", document.path("info").path("title").asText())
        assertEquals(
            "API documentation for the Books and Authors application",
            document.path("info").path("description").asText(),
        )
        assertEquals("1.0.0", document.path("info").path("version").asText())
    }

    @Test
    fun `swagger ui entry is reachable or redirects`() {
        val response = mockMvc.perform(get("/swagger-ui/index.html")).andReturn().response

        assertTrue(response.status in 200..399, "Expected a successful response or redirect, got ${response.status}")
    }

    @Test
    fun `graphiql serves the bundled editor configured for the graphql endpoint`() {
        val initialResponse = mockMvc.perform(get("/graphiql")).andReturn().response
        assertTrue(initialResponse.status in 200..399)
        val response = if (initialResponse.status in 300..399) {
            mockMvc.perform(get("/graphiql?path=/graphql")).andReturn().response
        } else {
            initialResponse
        }

        assertEquals(200, response.status)
        assertTrue(response.contentType.orEmpty().startsWith("text/html"))
        assertTrue(response.contentAsString.contains("<title>GraphiQL</title>"))
        assertTrue(response.contentAsString.contains("createGraphiQLFetcher"))
        assertTrue(response.contentAsString.contains("\"/graphql\""))
    }

    @Test
    fun `graphql http endpoint still handles an operation`() {
        val response = mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"query":"query { authors { id name } }"}"""),
        ).andReturn().response

        assertEquals(200, response.status)
        assertTrue(objectMapper.readTree(response.contentAsString).path("data").path("authors").isArray)
    }

    @Test
    fun `graphql http endpoint executes a mutation and returns errors for invalid operations`() {
        bookRepository.deleteAllInBatch()
        authorRepository.deleteAllInBatch()
        val author = authorRepository.save(Author(id = "graphiql-test-author", name = "GraphiQL Test Author"))
        val addResponse = executeGraphQl(
            "mutation { addBook(input: { title: \"GraphiQL test book\", authorId: \"${author.id}\" }) { id title author { id name } } }",
        )
        val addedBook = addResponse.path("data").path("addBook")
        val bookId = addedBook.path("id").asText()

        assertTrue(bookId.isNotBlank())
        assertEquals("GraphiQL test book", addedBook.path("title").asText())
        assertEquals(author.id, addedBook.path("author").path("id").asText())

        val updateResponse = executeGraphQl(
            "mutation { updateBook(id: \"$bookId\", input: { title: \"Updated in GraphiQL\" }) { id title } }",
        )
        assertEquals("Updated in GraphiQL", updateResponse.path("data").path("updateBook").path("title").asText())

        val deleteResponse = executeGraphQl("mutation { deleteBook(id: \"$bookId\") }")
        assertTrue(deleteResponse.path("data").path("deleteBook").asBoolean())

        val errorResponse = executeGraphQl("mutation { deleteBook(id: \"missing-graphiql-book\") }")
        assertTrue(errorResponse.path("errors").isArray)
        assertTrue(errorResponse.path("errors").size() > 0)
        assertTrue(authorRepository.existsById(author.id))
        assertEquals(0, bookRepository.count())
    }

    private fun executeGraphQl(query: String) = objectMapper.readTree(
        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(mapOf("query" to query))),
        ).andReturn().response.contentAsString,
    )
}

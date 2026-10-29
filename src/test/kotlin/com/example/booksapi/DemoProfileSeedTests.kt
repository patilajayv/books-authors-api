package com.example.booksapi

import com.example.booksapi.author.AuthorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.ActiveProfiles
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("demo")
class DemoProfileSeedTests {
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var dataSource: DataSource

    @Test
    fun `demo profile provisions the deterministic author seed`() {
        val author = authorRepository.findById("demo-author-001").orElseThrow()

        assertEquals("Demo Author", author.name)
    }

    @Test
    fun `demo seed can be reapplied without duplicate authors`() {
        ResourceDatabasePopulator(ClassPathResource("demo-data.sql")).execute(dataSource)

        assertEquals(1, authorRepository.count())
        assertTrue(authorRepository.existsById("demo-author-001"))
    }
}

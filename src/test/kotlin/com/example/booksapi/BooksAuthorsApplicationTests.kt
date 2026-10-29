package com.example.booksapi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.junit.jupiter.api.Assertions.assertFalse
import com.example.booksapi.author.AuthorRepository

@SpringBootTest
@ActiveProfiles("default")
class BooksAuthorsApplicationTests {
    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Test
    fun contextLoads() {
        assertFalse(authorRepository.existsById("demo-author-001"))
    }
}

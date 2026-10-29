package com.example.booksapi.book

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BookRepository : JpaRepository<Book, String> {
    override fun findAll(pageable: Pageable): Page<Book>
}

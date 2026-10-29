package com.example.booksapi.author

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorQueryService(
    private val authorRepository: AuthorRepository,
) {
    @Transactional(readOnly = true)
    fun findAll(): List<Author> = authorRepository.findAll()

    @Transactional(readOnly = true)
    fun findById(id: String): Author? = authorRepository.findById(id).orElse(null)

    @Transactional(readOnly = true)
    fun findAllByIds(ids: Iterable<String>): List<Author> = authorRepository.findAllById(ids)
}

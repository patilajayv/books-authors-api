package com.example.booksapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BooksAuthorsApplication

fun main(args: Array<String>) {
    runApplication<BooksAuthorsApplication>(*args)
}

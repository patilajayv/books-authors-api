package com.example.booksapi.book

import org.springframework.graphql.data.ArgumentValue

data class BookInput(
    val title: String,
    val authorId: String,
)

data class UpdateBookInput(
    val title: ArgumentValue<String?> = ArgumentValue.omitted(),
    val authorId: ArgumentValue<String?> = ArgumentValue.omitted(),
)

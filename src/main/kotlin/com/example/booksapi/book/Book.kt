package com.example.booksapi.book

import com.example.booksapi.author.Author
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "books")
open class Book {
    @Id
    @Column(nullable = false, updatable = false)
    open var id: String = ""

    @Column(nullable = false)
    open var title: String = ""

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    open lateinit var author: Author

    constructor()

    constructor(id: String, title: String, author: Author) : this() {
        this.id = id
        this.title = title
        this.author = author
    }
}

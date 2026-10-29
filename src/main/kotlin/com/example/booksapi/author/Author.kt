package com.example.booksapi.author

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "authors")
open class Author {
    @Id
    @Column(nullable = false, updatable = false)
    open var id: String = ""

    @Column(nullable = false)
    open var name: String = ""

    constructor()

    constructor(id: String, name: String) : this() {
        this.id = id
        this.name = name
    }
}

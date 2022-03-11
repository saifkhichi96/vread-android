package com.saifkhichi.books.model

enum class Format {
    Paperback,
    Hardcover,
    Audiobook,
    EBook,
    Other;

    companion object {
        fun fromString(string: String): Format {
            return when (string) {
                "Paperback" -> Paperback
                "Hardcover" -> Hardcover
                "Audiobook" -> Audiobook
                "EBook" -> EBook
                else -> Other
            }
        }
    }

}
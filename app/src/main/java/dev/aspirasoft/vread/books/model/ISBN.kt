package dev.aspirasoft.vread.books.model


/**
 * The [ISBN](https://en.wikipedia.org/wiki/International_Standard_Book_Number) identifier.
 *
 * The ISBN-10 and ISBN-13 are 10 and 13-digit numeric codes respectively for identifying books.
 * ISBN-10 is a legacy format that was replaced by ISBN-13 in 2007. This class can take either
 * format as input but the internal representation is always ISBN-13. Hyphens and spaces are
 * ignored, and ISBN-10 is converted to ISBN-13.
 *
 * Invalid ISBNs are not supported by this class, and an [IllegalArgumentException] will be thrown
 * if an invalid ISBN is passed to the constructor.
 *
 * @author AspiraSoft
 * @since 0.0.1
 *
 * @param value An ISBN-10 or ISBN-13 number, with or without hyphens.
 * @throws IllegalArgumentException If the value is not a valid ISBN-10 or ISBN-13 number.
 */
class ISBN(value: String) {

    /**
     * The internal representation of the ISBN, always a valid ISBN-13 number without hyphens.
     */
    private val value: String

    init {
        val isbn = value.replace("-", "").replace(" ", "")
        this.value = when (isbn.length) {
            10 -> when (isValidISBN10(isbn)) {
                true -> convertISBN10To13(isbn)
                else -> throw IllegalArgumentException("Invalid ISBN-13 number")
            }

            13 -> when (isValidISBN13(isbn)) {
                true -> isbn
                else -> throw IllegalArgumentException("Invalid ISBN-13 number")
            }

            else -> throw IllegalArgumentException("ISBN must be 10 or 13 digits long")
        }
    }

    /**
     * Checks if the given ISBN-10 number is valid.
     *
     * @param isbn The ISBN-10 number to check.
     * @return True if the number is valid, false otherwise.
     *
     * @see [https://en.wikipedia.org/wiki/ISBN#ISBN-10_check_digit_calculation]
     */
    private fun isValidISBN10(value: String): Boolean {
        if (value.length != 10) {
            return false
        }

        var sum = 0
        for (i in 0..9) {
            val c = value[i]
            sum += when (c) {
                'X' -> 10 * (10 - i)
                in '0'..'9' -> (c - '0') * (10 - i)
                else -> return false
            }
        }
        return sum % 11 == 0
    }

    /**
     * Checks if the given ISBN-13 number is valid.
     *
     * @param value The ISBN-13 number to check.
     * @return True if the number is valid, false otherwise.
     *
     * @see [https://en.wikipedia.org/wiki/ISBN#ISBN-13_check_digit_calculation]
     */
    private fun isValidISBN13(value: String): Boolean {
        // Check length (must be 13)
        if (value.length != 13) return false

        // Check that it starts with 978 or 979
        if (value.substring(0, 3) !in listOf("978", "979")) return false

        // Compute the check digit
        var sum = 0
        for (i in 0..11) {
            val c = value[i]
            sum += when (c) {
                in '0'..'9' -> (c - '0') * (if (i % 2 == 0) 1 else 3)
                else -> return false
            }
        }
        val r = 10 - sum % 10

        // Check that the check digit is correct
        val checkDigit = if (r == 10) '0' else '0' + r
        return value[12] == checkDigit
    }

    private fun convertISBN10To13(value: String): String {
        // If already ISBN-13, return as-is
        if (isValidISBN13(value)) return value

        // If not ISBN-10, throw exception
        if (!isValidISBN10(value)) throw IllegalArgumentException("Invalid ISBN-10")

        // Convert ISBN-10 to ISBN-13
        var isbn13: String = value
        isbn13 = "978" + isbn13.substring(0, 9)
        var d: Int

        var sum = 0
        for (i in isbn13.indices) {
            d = if (i % 2 == 0) 1 else 3
            sum += (isbn13[i].code - 48) * d
        }
        sum = 10 - sum % 10
        isbn13 += sum

        return isbn13
    }

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass == String::class.java) return value == ISBN(other as String).value
        if (javaClass != other?.javaClass) return false
        if (value == (other as ISBN).value) return true
        return false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}
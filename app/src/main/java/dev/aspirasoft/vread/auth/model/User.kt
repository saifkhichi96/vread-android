package dev.aspirasoft.vread.auth.model

import dev.aspirasoft.vread.core.model.Viewable

data class User(
    var uid: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var email: String = "",
    var joinedOn: String = "",

    var bio: String? = null,
    var city: String? = null,
    var country: String? = null,
    var dateOfBirth: String? = null,
    var phone: String? = null,
    var website: String? = null,
) : Viewable() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as User

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

}
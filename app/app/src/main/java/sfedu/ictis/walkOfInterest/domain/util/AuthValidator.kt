package sfedu.ictis.walkOfInterest.domain.util

object AuthValidator {
    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$".toRegex()

    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 64
    const val MAX_FIELD_LENGTH = 50 // Для имени, фамилии, логина
    const val MAX_EMAIL_LENGTH = 254

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() &&
                email.length <= MAX_EMAIL_LENGTH &&
                !email.contains(" ") &&
                EMAIL_REGEX.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH &&
                !password.contains(" ")
    }

    fun isValidUsername(username: String): Boolean {
        return username.isNotBlank() &&
                username.length <= MAX_FIELD_LENGTH &&
                !username.contains(" ") &&
                username.all { it.isLetterOrDigit() || it == '_' }
    }

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() &&
                name.length <= MAX_FIELD_LENGTH &&
                name.all { it.isLetter() || it == '-' }
    }

    fun isNoneBlank(vararg fields: String): Boolean {
        return fields.all { it.isNotBlank() }
    }
}
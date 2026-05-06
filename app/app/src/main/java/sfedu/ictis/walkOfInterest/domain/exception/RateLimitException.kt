package sfedu.ictis.walkOfInterest.domain.exception

class RateLimitException(
    message: String,
    val retryAfterSeconds: Long?
) : Exception(message)

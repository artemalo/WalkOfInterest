package sfedu.ictis.walkOfInterest.domain.model

enum class ReviewsSortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST;

    fun toggle(): ReviewsSortOrder = if (this == NEWEST_FIRST) OLDEST_FIRST else NEWEST_FIRST
}
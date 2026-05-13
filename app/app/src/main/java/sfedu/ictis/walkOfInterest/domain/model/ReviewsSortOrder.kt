package sfedu.ictis.walkOfInterest.domain.model

enum class ReviewsSortOrder {
    NEWEST_FIRST,
    POPULAR;

    fun toggle(): ReviewsSortOrder = if (this == NEWEST_FIRST) POPULAR else NEWEST_FIRST
}
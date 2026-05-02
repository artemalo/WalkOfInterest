package sfedu.ictis.walkOfInterest.presentation.poi

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.ReviewsSortOrder

data class PoiUiState(
    val poi: DomainPoiInfo? = null,
    val reviews: List<DomainReview> = emptyList(),
    val sortOrder: ReviewsSortOrder = ReviewsSortOrder.NEWEST_FIRST,

    val isPoiLoading: Boolean = false,
    val isReviewsLoading: Boolean = false
) {
    val sortedReviews: List<DomainReview>
        get() = when (sortOrder) {
            ReviewsSortOrder.NEWEST_FIRST -> reviews.sortedByDescending { it.createdAtMillis }
            ReviewsSortOrder.OLDEST_FIRST -> reviews.sortedBy { it.createdAtMillis }
        }
}

sealed class PoiEvent {
    data class ShowError(val message: String) : PoiEvent()
}
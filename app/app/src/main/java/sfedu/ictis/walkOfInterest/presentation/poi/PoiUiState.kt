package sfedu.ictis.walkOfInterest.presentation.poi

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.ReviewsSortOrder

data class PoiUiState(
    val poi: DomainPoiInfo? = null,
    val reviews: List<DomainReview> = emptyList(),
    val sortOrder: ReviewsSortOrder = ReviewsSortOrder.NEWEST_FIRST,

    val currentUsername: String? = null,

    val isPoiLoading: Boolean = false,
    val isReviewsLoading: Boolean = false
) {
    val sortedReviews: List<DomainReview>
        get() = when (sortOrder) {
            ReviewsSortOrder.NEWEST_FIRST -> reviews.sortedByDescending { it.createdAtMillis }
            ReviewsSortOrder.OLDEST_FIRST -> reviews.sortedBy { it.createdAtMillis }
        }

    val myReview: DomainReview?
        get() {
            val name = currentUsername?.takeIf { it.isNotBlank() } ?: return null
            return reviews.firstOrNull { it.authorUsername.equals(name, ignoreCase = true) }
        }
}

sealed class PoiEvent {
    data class ShowError(val message: String) : PoiEvent()
    data class OpenReviewMake(
        val poiId: Long,
        val poiName: String?,
        val poiAddress: String?,
        val existingReview: DomainReview?
    ) : PoiEvent()
}
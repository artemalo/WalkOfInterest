package sfedu.ictis.walkOfInterest.presentation.profile

import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.model.ReviewsSortOrder

data class ProfileUiState(
    val profile: DomainUserProfile? = null,
    val reviews: List<DomainReview> = emptyList(),
    val sortOrder: ReviewsSortOrder = ReviewsSortOrder.NEWEST_FIRST,

    val isLoadingReviews: Boolean = false,
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,

    val nicknameError: String? = null,
    val isUpdatingNickname: Boolean = false,
    val isUpdatingProfile: Boolean = false,

    val isLoggingOut: Boolean = false
) {
    val sortedReviews: List<DomainReview>
        get() = when (sortOrder) {
            ReviewsSortOrder.NEWEST_FIRST -> reviews.sortedByDescending { it.createdAtMillis }
            ReviewsSortOrder.OLDEST_FIRST -> reviews.sortedBy { it.createdAtMillis }
        }
}

sealed class ProfileEvent {
    data class ShowError(val message: String) : ProfileEvent()
    data object EmptyProfile : ProfileEvent()
    data object NicknameUpdated : ProfileEvent()
    data object CloseEditScreen : ProfileEvent()
    data object LoggedOut : ProfileEvent()
}
package sfedu.ictis.walkOfInterest.presentation.poi.review

data class ReviewMakeUiState(
    val poiId: Long = -1L,
    val poiName: String? = null,
    val poiAddress: String? = null,

    val rating: Int = 0,
    val content: String = "",

    val isEditMode: Boolean = false,
    val isSaving: Boolean = false,
    val ratingError: String? = null
)

sealed class ReviewMakeEvent {
    data class ShowError(val message: String) : ReviewMakeEvent()
    object Saved : ReviewMakeEvent()
}
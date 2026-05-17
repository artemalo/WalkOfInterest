package sfedu.ictis.walkOfInterest.presentation.poi.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.GetPoiByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.UpsertMyReviewUseCase

class ReviewMakeViewModel(
    private val upsertMyReviewUseCase: UpsertMyReviewUseCase,
    private val getPoiByIdUseCase: GetPoiByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewMakeUiState())
    val uiState: StateFlow<ReviewMakeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ReviewMakeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ReviewMakeEvent> = _events.asSharedFlow()

    private var initialized = false

    fun init(
        poiId: Long,
        poiName: String?,
        poiAddress: String?,
        existingRating: Int?,
        existingContent: String?
    ) {
        if (initialized) return
        initialized = true

        _uiState.update {
            it.copy(
                poiId = poiId,
                poiName = poiName,
                poiAddress = poiAddress,
                rating = existingRating?.coerceIn(0, 5) ?: 0,
                content = existingContent.orEmpty(),
                isEditMode = existingRating != null
            )
        }

        loadPoiPhoto(poiId)
    }

    fun onRatingSelected(value: Int) {
        _uiState.update { it.copy(rating = value.coerceIn(0, 5), ratingError = null) }
    }

    fun onContentChanged(text: String) {
        _uiState.update { it.copy(content = text) }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        if (state.isSaving) return

        if (state.rating !in 1..5) {
            _uiState.update { it.copy(ratingError = "Поставьте оценку от 1 до 5") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = upsertMyReviewUseCase(
                poiId = state.poiId,
                rating = state.rating,
                content = state.content
            )

            result.onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(ReviewMakeEvent.Saved)
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(ReviewMakeEvent.ShowError(e.message ?: "Не удалось сохранить отзыв"))
            }
        }
    }



    private fun loadPoiPhoto(poiId: Long) {
        viewModelScope.launch {
            getPoiByIdUseCase(poiId)
                .onSuccess { poiInfo ->
                    _uiState.update { it.copy(photoUrl = poiInfo.photoUrl) }
                }
                .onFailure {
                    _uiState.update { it.copy(photoUrl = null) }
                }
        }
    }
}
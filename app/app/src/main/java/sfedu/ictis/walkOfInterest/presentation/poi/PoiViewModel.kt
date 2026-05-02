package sfedu.ictis.walkOfInterest.presentation.poi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.usecase.GetPoiByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetPoiReviewsUseCase

class PoiViewModel(
    private val getPoiByIdUseCase: GetPoiByIdUseCase,
    private val getPoiReviewsUseCase: GetPoiReviewsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PoiUiState())
    val uiState: StateFlow<PoiUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PoiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PoiEvent> = _events.asSharedFlow()

    private var loadedId: Long? = null

    fun load(poiId: Long) {
        if (loadedId == poiId && _uiState.value.poi != null) return
        loadedId = poiId

        _uiState.update {
            it.copy(isPoiLoading = true, isReviewsLoading = true)
        }

        viewModelScope.launch {
            val poiDeferred = async { getPoiByIdUseCase(poiId) }
            val reviewsDeferred = async { getPoiReviewsUseCase(poiId) }

            poiDeferred.await()
                .onSuccess { poi ->
                    _uiState.update { it.copy(poi = poi, isPoiLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isPoiLoading = false) }
                    _events.emit(PoiEvent.ShowError(e.message ?: "Не удалось загрузить точку"))
                }

            reviewsDeferred.await()
                .onSuccess { reviews ->
                    _uiState.update { it.copy(reviews = reviews, isReviewsLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isReviewsLoading = false) }
                    _events.emit(PoiEvent.ShowError(e.message ?: "Не удалось загрузить отзывы"))
                }
        }
    }

    fun preload(name: String?, rating: Double?, count: Int?) {
        val current = _uiState.value.poi
        if (current != null) return

        _uiState.update {
            it.copy(
                poi = DomainPoiInfo(
                    id = loadedId ?: -1L,
                    point = null,
                    name = name,
                    description = null,
                    tags = emptyList(),
                    status = sfedu.ictis.walkOfInterest.domain.model.PoiStatus.APPROVED,
                    rating = rating ?: 0.0,
                    countRate = count ?: 0
                )
            )
        }
    }

    fun toggleSortOrder() {
        _uiState.update { it.copy(sortOrder = it.sortOrder.toggle()) }
    }
}
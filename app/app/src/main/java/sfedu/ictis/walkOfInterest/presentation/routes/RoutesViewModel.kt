package sfedu.ictis.walkOfInterest.presentation.routes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.usecase.GetCurrentTripUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetMapCenterUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetRoutesUseCase

class RoutesViewModel(
    private val getMapCenterUseCase: GetMapCenterUseCase,
    private val getCurrentTripUseCase: GetCurrentTripUseCase,
    private val getRoutesUseCase: GetRoutesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoutesUiState())
    val uiState: StateFlow<RoutesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RoutesEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    val defaultCenter: DomainPoint get() = getMapCenterUseCase()

    init {
        viewModelScope.launch {
            delay(100)
            loadTripData()
        }
    }

    private suspend fun loadTripData() {
        val trip = getCurrentTripUseCase()

        if (trip == null) {
            _events.emit(RoutesEvent.ShowError("Данные о поездке не найдены"))
            return
        }

        _uiState.update { it.copy(
            trip = trip,
            isLoading = true
        )}

        val str = trip.selectedPois.joinToString(separator = "\n") { poi ->
            "${poi.id}, ${poi.lat}, ${poi.lon}, ${poi.categoryId}"
        }
        Log.i("LOAD", str)

        val result = getRoutesUseCase(trip)

        result.onSuccess { loadedRoutes ->
            _uiState.update { state ->
                state.copy(
                    routes = loadedRoutes,
                    isLoading = false
                )
            }

            if (loadedRoutes.isEmpty()) {
                _events.emit(RoutesEvent.ShowError("Вернулся пустой список маршрутов"))
            }
        }.onFailure { error ->
            _uiState.update { it.copy(isLoading = false) }
            _events.emit(RoutesEvent.ShowError(error.message ?: "Не удалось сгенерировать маршрут"))
        }

    }

    fun selectRoute(route: DomainRoute) {
        val points = route.points.map { DomainPoint(it.lat, it.lon) }
        _uiState.update { it.copy(route = points) }

        viewModelScope.launch {
            _events.emit(RoutesEvent.CollapseBottomSheet)
        }
    }

    fun onBottomSheetStateChanged(newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED,
            BottomSheetBehavior.STATE_EXPANDED,
            BottomSheetBehavior.STATE_HIDDEN,
            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                _uiState.update { it.copy(bottomSheetState = newState) }
            }
        }
    }
}
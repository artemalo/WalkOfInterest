package sfedu.ictis.walkOfInterest.presentation.routes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import sfedu.ictis.walkOfInterest.domain.usecase.GetRoutesUseCase

class RoutesViewModel(
    private val repository: RouteRepository,
    private val getRoutesUseCase: GetRoutesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutesUiState())
    val uiState: StateFlow<RoutesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(100)
            loadTripData()
        }
    }

    private suspend fun loadTripData() {
        val trip = repository.getCurrentTrip()

        if (trip != null) {
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
                    _events.emit("Вернулся пустой список маршрутов")
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(error.message ?: "Не удалось сгенерировать маршрут")
            }
        } else {
            _events.emit("Данные о поездке не найдены в памяти")
        }

    }

    fun selectRoute(route: DomainRoute) {
        val points = route.points.map { DomainPoint(it.lat, it.lon) }
        _uiState.update { it.copy(route = points) }
    }
}
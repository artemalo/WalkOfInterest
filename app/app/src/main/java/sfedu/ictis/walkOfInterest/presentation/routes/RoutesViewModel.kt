package sfedu.ictis.walkOfInterest.presentation.routes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class RoutesViewModel(
    private val repository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutesUiState())
    val uiState: StateFlow<RoutesUiState> = _uiState.asStateFlow()

    init {
        loadTripData()
    }

    private fun loadTripData() {
        val trip = repository.getCurrentTrip()
        if (trip != null) {
            _uiState.update { it.copy(
                trip = trip,
                mapPoints = trip.selectedPois, // По умолчанию показываем все выбранные точки
                // Здесь будет загрузка вариантов маршрутов от бэкенда (GraphHopper)
                routes = emptyList()
            )}
        }
    }

    // Когда пользователь кликает на конкретный вариант маршрута в списке
    fun selectRoute(route: DomainRoute) {
        val points = route.pois.map {
            RoutePoint(it.id, it.lat, it.lon, 1) // Мапим во вью-модели
        }
        _uiState.update { it.copy(mapPoints = points) }
    }
}
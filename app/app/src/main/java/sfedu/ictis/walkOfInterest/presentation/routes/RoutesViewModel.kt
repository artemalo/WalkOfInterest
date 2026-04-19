package sfedu.ictis.walkOfInterest.presentation.routes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository

class RoutesViewModel(
    private val repository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutesUiState())
    val uiState: StateFlow<RoutesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    init {
        loadTripData()
    }

    private fun loadTripData() {
        val trip = repository.getCurrentTrip()
        if (trip != null) {
            val loadedRoutes = emptyList<DomainRoute>() // TODO

            _uiState.update { it.copy(
                trip = trip,
                mapPoints = trip.selectedPois,
                // TODO init routes
                routes = loadedRoutes
            )}

            if (loadedRoutes.isEmpty()) {
                viewModelScope.launch {
                    _events.emit("Не удалось сгенерировать маршрут")
                }
            }

            val str = trip.selectedPois.joinToString(separator = "\n") { poi ->
                "${poi.id}, ${poi.lat}, ${poi.lon}, ${poi.categoryId}"
            }
            Log.i("LOAD", str)
        }
    }

    fun selectRoute(route: DomainRoute) {
        val points = route.pois.map {
            RoutePoint(
                it.id,
                it.lat,
                it.lon,
                it.categoryId,
                it.name,
                it.nameCat,
                it.nameSubcat
            )
        }
        _uiState.update { it.copy(mapPoints = points) }
    }
}
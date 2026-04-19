package sfedu.ictis.walkOfInterest.presentation.routes

import android.util.Log
import androidx.lifecycle.ViewModel
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
                mapPoints = trip.selectedPois,
                // TODO init routes
                routes = emptyList()
            )}

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
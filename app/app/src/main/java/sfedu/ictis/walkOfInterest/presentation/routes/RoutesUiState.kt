package sfedu.ictis.walkOfInterest.presentation.routes

import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint

data class RoutesUiState(
    val trip: DomainTrip? = null,
    val routes: List<DomainRoute> = emptyList(),

    val mapPoints: List<RoutePoint> = emptyList(),
    val isLoading: Boolean = false
)
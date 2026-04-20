package sfedu.ictis.walkOfInterest.presentation.routes

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip

data class RoutesUiState(
    val trip: DomainTrip? = null,
    val routes: List<DomainRoute> = emptyList(),

    val route: List<DomainPoint> = emptyList(),

    val isLoading: Boolean = false
)
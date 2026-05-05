package sfedu.ictis.walkOfInterest.presentation.details

import sfedu.ictis.walkOfInterest.domain.model.DomainTrip

data class TripDetailsUiState(
    val trip: DomainTrip? = null,
    val isLoading: Boolean = false,
    val notFound: Boolean = false
)
package sfedu.ictis.walkOfInterest.presentation.main

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

data class MainUiState(
    val pointFrom: DomainPoint? = null,
    val pointTo: DomainPoint? = null,
    val addressFrom: String = "Откуда",
    val addressTo: String = "Куда",
    val minTimeMinutes: Int? = null,
    val route: List<DomainPoint>? = null,
    val selectedTimeMinutes: Int = 0,
    val isLoading: Boolean = false,
    val isTimePickerEnabled: Boolean = false,
    val isCalculateEnabled: Boolean = false
)
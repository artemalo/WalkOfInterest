package sfedu.ictis.walkOfInterest.presentation.generate

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

data class GenerateUiState(
    val pointFrom: DomainPoint? = null,
    val pointTo: DomainPoint? = null,
    val addressFrom: String = "Откуда",
    val addressTo: String = "Куда",
    val route: List<DomainPoint>? = null,

    val minTimeMinutes: Int = 0,
    val selectedTimeMinutes: Int = 0,

    val maxPoiLimit: Int = 0,
    val selectedPoiCount: Int = 0,

    val isLoading: Boolean = false,
    val isTimePickerEnabled: Boolean = false,
    val isCalculateEnabled: Boolean = false
)
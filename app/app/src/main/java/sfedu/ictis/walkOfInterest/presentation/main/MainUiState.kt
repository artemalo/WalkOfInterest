package sfedu.ictis.walkOfInterest.presentation.main

import sfedu.ictis.walkOfInterest.data.model.PointDto

data class MainUiState(
    val pointFrom: PointDto? = null,
    val pointTo: PointDto? = null,
    val addressFrom: String = "Откуда",
    val addressTo: String = "Куда",
    val minTimeMinutes: Int? = null,
    val route: List<PointDto>? = null,
    val selectedTimeMinutes: Int = 0,
    val isLoading: Boolean = false,
    val isTimePickerEnabled: Boolean = false,
    val isCalculateEnabled: Boolean = false
)
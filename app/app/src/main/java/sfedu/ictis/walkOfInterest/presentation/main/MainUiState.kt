package sfedu.ictis.walkOfInterest.presentation.main

import sfedu.ictis.walkOfInterest.data.model.Coordinates

data class MainUiState(
    val pointFrom: Coordinates? = null,
    val pointTo: Coordinates? = null,
    val addressFrom: String = "Откуда",
    val addressTo: String = "Куда",
    val minTimeMinutes: Int? = null,
    val selectedTimeMinutes: Int = 0,
    val isLoading: Boolean = false,
    val isTimePickerEnabled: Boolean = false,
    val isCalculateEnabled: Boolean = false
)